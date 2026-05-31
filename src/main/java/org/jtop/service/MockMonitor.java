package org.jtop.service;

import java.util.List;
import org.jtop.model.DiskInfo;
import org.jtop.model.MemoryInfo;
import org.jtop.model.NetworkInfo;
import org.jtop.model.ProcessInfo;
import org.jtop.model.SystemSnapshot;

public class MockMonitor
		implements DataSource {

	private final int cores;

	public MockMonitor(int cores) {
		this.cores = cores;
	}

	@Override
	public SystemSnapshot getLatestSnapshot() {
		double[] testCpuLoadPerCore = new double[cores];
		long timestamp = System.currentTimeMillis();

		for (int i = 0; i < cores; i++) {
			testCpuLoadPerCore[i] = (double) i / (cores - 1);
		}

		MemoryInfo ram = new MemoryInfo(8L * 1024 * 1024 * 1024, 16L * 1024 * 1024 * 1024);
		MemoryInfo swap = new MemoryInfo(512L * 1024 * 1024, 2L * 1024 * 1024 * 1024);

		List<ProcessInfo> processes = List.of(
				new ProcessInfo(
						1,
						"root",
						20,
						0,
						500_000_000L,
						50_000_000L,
						10_000_000L,
						'S',
						0.0,
						0.3,
						1200_000L,
						"systemd",
						0L,
						0L
				),
				new ProcessInfo(
						1234,
						"sebas",
						20,
						0,
						2_000_000_000L,
						800_000_000L,
						50_000_000L,
						'S',
						45.2,
						5.0,
						3600_000L,
						"idea64",
						2_000_000L,
						500_000L
				),
				new ProcessInfo(
						5678,
						"sebas",
						20,
						0,
						1_500_000_000L,
						600_000_000L,
						40_000_000L,
						'S',
						23.1,
						3.7,
						2400_000L,
						"chrome",
						800_000L,
						200_000L
				),
				new ProcessInfo(
						9012,
						"sebas",
						20,
						0,
						800_000_000L,
						200_000_000L,
						20_000_000L,
						'R',
						10.5,
						1.2,
						900_000L,
						"java",
						5_000_000L,
						1_000_000L
				),
				new ProcessInfo(
						3456,
						"root",
						20,
						0,
						100_000_000L,
						10_000_000L,
						5_000_000L,
						'S',
						0.1,
						0.1,
						300_000L,
						"sshd",
						0L,
						10_000L
				)
		);

		long uptime = 3 * 24 * 3600L + 12 * 3600L + 4 * 60L + 22L;
		double[] loadAverage = {2.4, 1.8, 1.2};

		List<DiskInfo> diskInfos = List.of(
				new DiskInfo(
						"sda",
						150_000_000L,
						30_000_000L,
						500_000_000_000L,
						120_000_000_000L
				),
				new DiskInfo(
						"sdb",
						20_000_000L,
						5_000_000L,
						80_000_000_000L,
						15_000_000_000L
				)
		);

		List<NetworkInfo> networkInfos = List.of(
				new NetworkInfo(
						"eth0",
						"physical",
						5_000_000L,
						1_000_000L,
						100_000_000_000L,
						20_000_000_000L
				),
				new NetworkInfo("wlan0", "physical", 500_000L, 100_000L, 10_000_000_000L,
												2_000_000_000L),
				new NetworkInfo("lo", "loopback", 0L, 0L, 500_000_000L, 500_000_000L),
				new NetworkInfo("wg0", "vpn", 200_000L, 50_000L, 5_000_000_000L, 1_000_000_000L),
				new NetworkInfo("docker0", "virtual", 10_000L, 5_000L, 100_000_000L, 50_000_000L)
		);

		return new SystemSnapshot(
				testCpuLoadPerCore,
				null,
				timestamp,
				ram,
				swap,
				processes,
				uptime,
				loadAverage,
				"byzantium-pc",
				"Windows 11 10.0.26200",
				64,
				diskInfos,
				networkInfos
		);
	}

	@Override
	public void startPolling() {

	}
}
