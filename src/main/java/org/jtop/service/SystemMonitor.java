package org.jtop.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import org.jtop.model.DiskInfo;
import org.jtop.model.MemoryInfo;
import org.jtop.model.NetworkInfo;
import org.jtop.model.ProcessInfo;
import org.jtop.model.SystemSnapshot;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.GlobalMemory;
import oshi.hardware.HWDiskStore;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.hardware.NetworkIF;
import oshi.software.os.OSProcess;
import oshi.software.os.OperatingSystem;

public class SystemMonitor
		implements DataSource {

	private final CentralProcessor processor;
	private final GlobalMemory memory;
	private final Map<Integer, OSProcess> processMap;
	private final OperatingSystem os;
	private long[][] previousTicks;
	private final Map<String, long[]> previousDiskReadings = new HashMap<>();
	private final Map<String, long[]> previousNetworkReadings = new HashMap<>();
	private long previousPollTime;
	private final ScheduledExecutorService scheduler;
	private final AtomicReference<SystemSnapshot> latestSnapshot;

	private static final long POLLING_INITIAL_DELAY = 0;
	private static final long POLLING_INTERVAL = 500;

	private final HardwareAbstractionLayer hal;

	public SystemMonitor() {
		SystemInfo systemInfo = new SystemInfo();

		this.hal = systemInfo.getHardware();
		this.processor = hal.getProcessor();
		this.memory = hal.getMemory();
		this.processMap = new HashMap<Integer, OSProcess>();
		this.os = systemInfo.getOperatingSystem();
		this.scheduler = Executors.newSingleThreadScheduledExecutor();
		this.previousTicks = this.processor.getProcessorCpuLoadTicks();
		this.latestSnapshot = new AtomicReference<>(null);
		this.previousPollTime = System.currentTimeMillis();
	}

	public void startPolling() {
		scheduler.scheduleAtFixedRate(
				this::poll,
				POLLING_INITIAL_DELAY,
				POLLING_INTERVAL,
				TimeUnit.MILLISECONDS
		);
	}

	private void poll() {
		long updatedTimestamp = System.currentTimeMillis();
		long elapsed = updatedTimestamp - previousPollTime;
		double elapsedSecs = elapsed / 1000.0;

		double[] updatedCpuLoadBetweenTicks = processor.getProcessorCpuLoadBetweenTicks(previousTicks);
		previousTicks = processor.getProcessorCpuLoadTicks();

		MemoryInfo ram = new MemoryInfo(memory.getTotal() - memory.getAvailable(), memory.getTotal());

		MemoryInfo swap = new MemoryInfo(
				memory.getVirtualMemory().getSwapUsed(),
		                                 memory.getVirtualMemory().getSwapTotal()
		);

		List<OSProcess> currentProcesses = os.getProcesses();
		long totalRam = memory.getTotal();
		List<ProcessInfo> processList = new ArrayList<>();

		for (OSProcess process : currentProcesses) {
			OSProcess prev = processMap.get(process.getProcessID());

			double cpu = prev != null ? process.getProcessCpuLoadBetweenTicks(prev) * 100 : 0.0;
			double mem = (double) process.getResidentMemory() / totalRam * 100;
			Long shr = process.getResidentMemory() - process.getPrivateResidentMemory();

			long readSpeed = 0, writeSpeed = 0;
			if (prev != null && elapsedSecs > 0) {
				readSpeed = (long) (Math.max(0, process.getBytesRead() - prev.getBytesRead())
						/ elapsedSecs);
				writeSpeed = (long) (Math.max(0, process.getBytesWritten() - prev.getBytesWritten())
						/ elapsedSecs);
			}

			processList.add(new ProcessInfo(
					process.getProcessID(),
					process.getUser(),
					process.getPriority(),
					process.getPriority(),
					process.getVirtualSize(),
					process.getResidentMemory(),
					shr,
					stateChar(process.getState()),
					cpu,
					mem,
					process.getUserTime() + process.getKernelTime(),
					process.getName(),
					readSpeed,
					writeSpeed
			));

			processMap.put(process.getProcessID(), process);
		}

		processList.sort((a, b) -> Double.compare(b.cpuUsage(), a.cpuUsage()));

		long uptime = os.getSystemUptime();
		double[] loadAverage = processor.getSystemLoadAverage(3);

		String hostname = os.getNetworkParams().getHostName();
		String osInfo = os.getFamily() + " " + os.getVersionInfo().getVersion();
		int architecture = os.getBitness();

		List<DiskInfo> diskInfos = new ArrayList<>();
		for (HWDiskStore disk : hal.getDiskStores()) {
			disk.updateAttributes();
			long[] prev = previousDiskReadings.get(disk.getName());
			long readSpeed = 0, writeSpeed = 0;

			if (prev != null && elapsedSecs > 0) {
				readSpeed = (long) (Math.max(0, (disk.getReadBytes() - prev[0])) / elapsedSecs);
				writeSpeed = (long) (Math.max(0, (disk.getWriteBytes() - prev[1])) / elapsedSecs);
			}

			previousDiskReadings.put(
					disk.getName(),
					new long[]{disk.getReadBytes(), disk.getWriteBytes()}
			);

			diskInfos.add(new DiskInfo(
					disk.getName(),
			                           readSpeed,
			                           writeSpeed,
			                           disk.getReadBytes(),
			                           disk.getWriteBytes()
			));
		}

		List<NetworkInfo> networkInfos = new ArrayList<>();
		for (NetworkIF net : hal.getNetworkIFs()) {
			net.updateAttributes();
			long[] prev = previousNetworkReadings.get(net.getName());
			long receiveSpeed = 0, sentSpeed = 0;
			if (prev != null && elapsedSecs > 0) {
				receiveSpeed = (long) ((net.getBytesRecv() - prev[0]) / elapsedSecs);
				sentSpeed = (long) ((net.getBytesSent() - prev[1]) / elapsedSecs);
			}

			previousNetworkReadings.put(
					net.getName(),
					new long[]{net.getBytesRecv(), net.getBytesSent()}
			);

			networkInfos.add(new NetworkInfo(
					net.getName(),
			                                 classifyInterface(net),
			                                 receiveSpeed,
			                                 sentSpeed,
			                                 net.getBytesRecv(),
			                                 net.getBytesSent()
			));
		}

		previousPollTime = updatedTimestamp;

		SystemSnapshot updatedSystemSnapshot = new SystemSnapshot(
				updatedCpuLoadBetweenTicks,
				null,
				updatedTimestamp,
				ram,
				swap,
				processList,
				uptime,
				loadAverage,
				hostname,
				osInfo,
				architecture,
				diskInfos,
				networkInfos
		);

		latestSnapshot.set(updatedSystemSnapshot);
	}

	private char stateChar(OSProcess.State state) {
		return switch (state) {
			case RUNNING -> 'R';
			case SLEEPING -> 'S';
			case WAITING -> 'W';
			case ZOMBIE -> 'Z';
			case STOPPED -> 'T';
			default -> '?';
		};
	}

	private String classifyInterface(NetworkIF net) {
		String name = net.getName().toLowerCase();
		String mac = net.getMacaddr();

		if (name.equals("lo") || name.startsWith("lo0") || name.contains("loopback") || mac.equals(
				"00:00:00:00:00:00") || mac.isEmpty()) {
			return "loopback";
		}
		if (name.contains("docker") || name.contains("veth") || name.contains("virbr") || name.contains(
				"br-") || name.contains("vmnet")) {
			return "virtual";
		}
		if (name.contains("vpn") || name.contains("tun") || name.contains("wg")
				|| name.contains("ppp")) {
			return "vpn";
		}

		return "physical";
	}

	public SystemSnapshot getLatestSnapshot() {
		return latestSnapshot.get();
	}
}
