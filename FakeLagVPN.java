package NDLModz.Dev;

import android.net.VpnService;
import android.os.ParcelFileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import java.util.Random;
import java.util.UUID;

public class FakeLagVPN extends VpnService {

    public static volatile boolean isRunning = false;
    private ParcelFileDescriptor iface = null;
    private Thread thread = null;
    private boolean shouldStop = false;
    private Random random = new Random();

    private String currentFakeIP = "";
    private String sessionId = "";

    private static final String[] FREE_FIRE_PACKAGES = {
        "com.dts.freefireth",       
        "com.dts.freefiremax"      
    };

    private static final String[] ALL_TRAFFIC_ROUTES = {
        "0.0.0.0", "0.0.0.0/0", "0.0.0.0/1", "128.0.0.0/1",
        "0.0.0.0/2", "64.0.0.0/2", "128.0.0.0/2", "192.0.0.0/2",
        "0.0.0.0/3", "32.0.0.0/3", "64.0.0.0/3", "96.0.0.0/3",
        "128.0.0.0/3", "160.0.0.0/3", "192.0.0.0/3", "224.0.0.0/3",
        "10.0.0.0/8", "172.16.0.0/12", "192.168.0.0/16",
        "100.64.0.0/10", "169.254.0.0/16", "192.0.0.0/24",
        "192.0.2.0/24", "198.18.0.0/15", "198.51.100.0/24",
        "203.0.113.0/24", "240.0.0.0/4",
        "1.0.0.0/8", "2.0.0.0/8", "3.0.0.0/8", "4.0.0.0/8",
        "5.0.0.0/8", "6.0.0.0/8", "7.0.0.0/8", "8.0.0.0/8",
        "9.0.0.0/8", "11.0.0.0/8", "12.0.0.0/8", "13.0.0.0/8",
        "14.0.0.0/8", "15.0.0.0/8", "16.0.0.0/8", "17.0.0.0/8",
        "18.0.0.0/8", "19.0.0.0/8", "20.0.0.0/8", "21.0.0.0/8",
        "22.0.0.0/8", "23.0.0.0/8", "24.0.0.0/8", "25.0.0.0/8",
        "26.0.0.0/8", "27.0.0.0/8", "28.0.0.0/8", "29.0.0.0/8",
        "30.0.0.0/8", "31.0.0.0/8", "33.0.0.0/8", "34.0.0.0/8",
        "::", "::/0", "2000::/3", "fc00::/7",
        "fe80::/10", "ff00::/8", "::1/128",
        "2001:db8::/32", "2001:0::/32"
    };

    private static final String[] GAME_SERVERS = {
        "203.205.141.66", "203.205.141.67", "203.205.141.68", "203.205.141.69",
        "203.205.141.70", "203.205.141.71", "203.205.141.72", "203.205.141.73",
        "203.205.141.74", "203.205.141.75", "203.205.141.76", "203.205.141.77",
        "203.205.141.78", "203.205.141.79", "203.205.141.80", "203.205.141.81",
        "52.76.103.48", "52.76.103.49", "52.76.103.50", "52.76.103.51",
        "52.76.103.52", "52.76.103.53", "52.76.103.54", "52.76.103.55",
        "54.169.203.251", "54.169.203.252", "54.169.203.253", "54.169.203.254",
        "13.228.73.166", "13.228.73.167", "13.228.73.168", "13.228.73.169",
        "54.255.192.218", "54.255.192.219", "54.255.192.220", "54.255.192.221",
        "13.112.63.251", "13.112.63.252", "13.112.63.253", "13.112.63.254",
        "54.64.140.1", "54.64.140.2", "54.64.140.3", "54.64.140.4",
        "13.230.123.1", "13.230.123.2", "13.230.123.3", "13.230.123.4",
        "13.126.162.1", "13.126.162.2", "13.126.162.3", "13.126.162.4",
        "13.234.210.1", "13.234.210.2", "13.234.210.3", "13.234.210.4",
        "52.66.123.1", "52.66.123.2", "52.66.123.3", "52.66.123.4",
        "13.124.63.1", "13.124.63.2", "13.124.63.3", "13.124.63.4",
        "52.78.63.1", "52.78.63.2", "52.78.63.3", "52.78.63.4",
        "15.164.123.1", "15.164.123.2", "15.164.123.3", "15.164.123.4",
        "52.8.123.1", "52.8.123.2", "52.8.123.3", "52.8.123.4",
        "54.183.123.1", "54.183.123.2", "54.183.123.3", "54.183.123.4",
        "54.67.123.1", "54.67.123.2", "54.67.123.3", "54.67.123.4",
        "52.70.123.1", "52.70.123.2", "52.70.123.3", "52.70.123.4",
        "52.54.123.1", "52.54.123.2", "52.54.123.3", "52.54.123.4",
        "35.174.123.1", "35.174.123.2", "35.174.123.3", "35.174.123.4",
        "35.157.123.1", "35.157.123.2", "35.157.123.3", "35.157.123.4",
        "52.57.123.1", "52.57.123.2", "52.57.123.3", "52.57.123.4",
        "18.138.233.123", "18.138.233.124", "18.138.233.125", "18.138.233.126",
        "18.139.225.76", "18.139.225.77", "18.139.225.78", "18.139.225.79",
        "15.185.123.1", "15.185.123.2", "15.185.123.3", "15.185.123.4",
        "157.175.123.1", "157.175.123.2", "157.175.123.3", "157.175.123.4",
        "54.94.123.1", "54.94.123.2", "54.94.123.3", "54.94.123.4",
        "54.207.123.1", "54.207.123.2", "54.207.123.3", "54.207.123.4",
        "54.232.123.1", "54.232.123.2", "54.232.123.3", "54.232.123.4",
        "1.1.1.1", "1.0.0.1", "104.16.0.0", "104.17.0.0",
        "104.18.0.0", "104.19.0.0", "104.20.0.0", "104.21.0.0",
        "104.22.0.0", "104.23.0.0", "104.24.0.0", "104.25.0.0",
        "8.8.8.8", "8.8.4.4",
        "9.9.9.9", "149.112.112.112"
    };
    
    private static final int[] BLOCK_PORTS = {
        10012, 10013, 10014, 10015, 10016, 10017, 10018, 10019, 10020,
        20002, 20003, 20004, 20005, 20006, 20007, 20008, 20009, 20010,
        17500, 17501, 17502, 17503, 17504, 17505, 17506, 17507, 17508,
        13000, 13001, 13002, 13003, 13004, 13005, 13006, 13007, 13008, 13009,
        14000, 14001, 14002, 14003, 14004, 14005, 14006, 14007, 14008, 14009,
        15000, 15001, 15002, 15003, 15004, 15005, 15006, 15007, 15008, 15009,
        21000, 21001, 21002, 21003, 21004, 21005, 21006, 21007, 21008, 21009,
        22000, 22001, 22002, 22003, 22004, 22005, 22006, 22007, 22008, 22009,
        23000, 23001, 23002, 23003, 23004, 23005, 23006, 23007, 23008, 23009,
        16000, 16001, 16002, 16003, 16004, 16005, 16006, 16007, 16008, 16009,
        17000, 17001, 17002, 17003, 17004, 17005, 17006, 17007, 17008, 17009,
        18000, 18001, 18002, 18003, 18004, 18005, 18006, 18007, 18008, 18009,
        19000, 19001, 19002, 19003, 19004, 19005, 19006, 19007, 19008, 19009,
        24000, 24001, 24002, 24003, 24004, 24005, 24006, 24007, 24008, 24009,
        25000, 25001, 25002, 25003, 25004, 25005, 25006, 25007, 25008, 25009,
        26000, 26001, 26002, 26003, 26004, 26005, 26006, 26007, 26008, 26009,
        27000, 27001, 27002, 27003, 27004, 27005, 27006, 27007, 27008, 27009,
        28000, 28001, 28002, 28003, 28004, 28005, 28006, 28007, 28008, 28009,
        29000, 29001, 29002, 29003, 29004, 29005, 29006, 29007, 29008, 29009,
        80, 443, 8080, 8443, 53, 123, 993, 995, 587, 465
    };

    @Override
    public IBinder onBind(Intent intent) {
        return super.onBind(intent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && "STOP_VPN".equals(intent.getAction())) {
            stopVpn();
            return START_NOT_STICKY;
        }

        if (isRunning) {
            return START_STICKY;
        }

        isRunning = true;
        shouldStop = false;

        sessionId = UUID.randomUUID().toString().substring(0, 8);
        currentFakeIP = generateDynamicIP();
        thread = new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						if (Build.VERSION.SDK_INT >= 26) {
							startForeground(1, NotificationHelper.createNotification(FakeLagVPN.this));
						}
						
						Builder builder = new Builder();
						builder.setSession("FakeLag VPN" + sessionId);
						builder.setMtu(1500);
						builder.addAddress(currentFakeIP, 24);
						builder.addAddress("172.16.0.2", 32);
						builder.addAddress("192.168.1.100", 24);
						builder.addAddress("10.0.0.100", 24);
						builder.addAddress("10.8.0.1", 24);
						builder.addRoute("1.1.1.1", 32);
						builder.addRoute("::", 0);
						for (String packageName : FREE_FIRE_PACKAGES) {
							try {
								builder.addAllowedApplication(packageName);
							} catch (Exception e) {
							}
						}

						for (String route : ALL_TRAFFIC_ROUTES) {
							try {
								builder.addRoute(route, 0);
							} catch (Exception e) {
							}
						}
					
						for (String server : GAME_SERVERS) {
							builder.addRoute(server, 32);
						}

						builder.addRoute("192.168.0.0", 16);
						builder.addRoute("10.0.0.0", 8);
						builder.addRoute("172.16.0.0", 12);
						iface = builder.establish();
						if (iface == null)  {
							isRunning = false;
							return;
						}

						FileInputStream in = new FileInputStream(iface.getFileDescriptor());
						FileOutputStream out = new FileOutputStream(iface.getFileDescriptor());

						byte[] buffer = new byte[65536];

						long startTime = System.currentTimeMillis();
						long totalPackets = 0;
						long blockedPackets = 0;
						long delayedPackets = 0;
						int blockCounter = 0;
						int packetSequence = 0;

						while (!shouldStop && !Thread.currentThread().isInterrupted()) {
							try {
								int length = in.read(buffer);

								if (length > 0) {
									totalPackets++;
									packetSequence++;
									blockCounter++;
									boolean isGamePacket = isGamePacket(buffer, length);
									boolean isFromServer = isFromGameServer(buffer, length);
									if (isGamePacket) {
										if (isFromServer && shouldBlockPacket(blockCounter)) {
											blockedPackets++;
											Thread.sleep(150 + random.nextInt(200));
											continue;
										}
										
										delayedPackets++;
										int delayTime = 400 + random.nextInt(300); 
										Thread.sleep(delayTime);

										out.write(buffer, 0, length);
										out.flush();

										if (delayedPackets % 20 == 0) {
										}
									} else {
										Thread.sleep(10 + random.nextInt(20));
										out.write(buffer, 0, length);
										out.flush();
									}
								}

								if (System.currentTimeMillis() - startTime > 3000) {
									double blockRate = totalPackets > 0 ? (blockedPackets * 100.0 / totalPackets) : 0;
									startTime = System.currentTimeMillis();
								}

							} catch (InterruptedException e) {
								Thread.currentThread().interrupt();
								break;
							} catch (Exception e) {
								break;
							}
						}

						try { in.close(); } catch (Exception e) {}
						try { out.close(); } catch (Exception e) {}

					} catch (Exception e)  {
					} finally {
						cleanup();
					}
				}
			});

        thread.start();
        return START_STICKY;
    }

    private String generateDynamicIP() {
        Random r = new Random();
        return String.format("10.%d.%d.%d", 
							 r.nextInt(254) + 1,
							 r.nextInt(254) + 1,
							 r.nextInt(254) + 1);
    }
	
    private boolean isGamePacket(byte[] packet, int length) {
        if (length < 20) return false;

        try {
            int version = (packet[0] >> 4) & 0x0F;
            if (version == 4) {
                int headerLength = (packet[0] & 0x0F) * 4;

                if (length >= headerLength + 20) {
                    int destPort = ((packet[headerLength + 2] & 0xFF) << 8) | 
                        (packet[headerLength + 3] & 0xFF);

                    int srcPort = ((packet[headerLength] & 0xFF) << 8) | 
                        (packet[headerLength + 1] & 0xFF);
						
                    for (int port : BLOCK_PORTS) {
                        if (destPort == port || srcPort == port) {
                            return true;
                        }
                    }

                    String destIp = String.format("%d.%d.%d.%d", 
												  packet[16] & 0xFF,
												  packet[17] & 0xFF, 
												  packet[18] & 0xFF,
												  packet[19] & 0xFF);

                    String srcIp = String.format("%d.%d.%d.%d", 
												 packet[12] & 0xFF,
												 packet[13] & 0xFF, 
												 packet[14] & 0xFF,
												 packet[15] & 0xFF);

                    for (String server : GAME_SERVERS) {
                        if (destIp.equals(server) || srcIp.equals(server)) {
                            return true;
                        }
                    }
                }
            }
        } catch (Exception e) {
        }

        return false;
    }

    private boolean isFromGameServer(byte[] packet, int length) {
        if (length < 20) return false;

        try {
            int version = (packet[0] >> 4) & 0x0F;
            if (version == 4) {
                String srcIp = String.format("%d.%d.%d.%d", 
											 packet[12] & 0xFF,
											 packet[13] & 0xFF, 
											 packet[14] & 0xFF,
											 packet[15] & 0xFF);

                for (String server : GAME_SERVERS) {
                    if (srcIp.equals(server)) {
                        return true;
                    }
                }
            }
        } catch (Exception e) {
        }

        return false;
    }

    private boolean shouldBlockPacket(int counter) {
        if (counter % 3 == 0) {
            return true;
        }

        if (random.nextFloat() < 0.25f) { 
            return true;
        }

        if (counter % 8 >= 6) {
            return true;
        }

        return false;
    }

    private void cleanup() {
        shouldStop = true;

        try {
            if (iface != null) {
                iface.close();
                iface = null;
            }
        } catch (Exception e) {
        }

        isRunning = false;
        stopForeground(true);
        stopSelf();
    }

    private void stopVpn() {
        shouldStop = true;
        if (thread != null) {
            thread.interrupt();
            try {
                thread.join(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        cleanup();
    }

    @Override
    public void onRevoke() {
        stopVpn();
        super.onRevoke();
    }

    @Override
    public void onDestroy() {
        stopVpn();
        super.onDestroy();
    }
}