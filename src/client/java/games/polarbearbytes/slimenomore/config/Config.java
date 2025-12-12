package games.polarbearbytes.slimenomore.config;

public final class Config {

    public static final boolean USE_STUB =
            Boolean.getBoolean("slimenomore.useStubConfig") ||
                    "true".equalsIgnoreCase(System.getenv("SLIMENOMORE_USE_STUB"));

    private static final ConfigProvider PROVIDER;

    static {
        if (USE_STUB) {
            PROVIDER = new StubConfigProvider();
        } else {
            PROVIDER = new AutoConfigProvider();
        }
    }

    public static ConfigProvider get() {
        return PROVIDER;
    }
}