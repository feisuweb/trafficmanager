package team.monroe.org.trafficmanager.entities;


import team.monroe.org.trafficmanager.R;

public enum  DeviceType {

    UNKNOWN("Unknown", R.drawable.android_computer_transperent),
    PHONE("Phone", R.drawable.type_smartphone),
    TABLET("Tablet", R.drawable.type_tablet),
    LAPTOP("Laptop", R.drawable.type_laptop),
    DESKTOP("Desktop", R.drawable.type_desktop),
    TV("Tv", R.drawable.type_tv),
    DEVICE("Device", R.drawable.type_router);

    public final String title;
    public final int drawableId;

    DeviceType(String title, int drawableId) {
        this.title = title;
        this.drawableId = drawableId;
    }

    public static DeviceType by(int ord) {
        return DeviceType.values()[ord];
    }

    /*
    public static int getDrawableId(int deviceType){
        switch (deviceType){
            case DEVICE_UNKNOWN: return R.drawable.android_computer_transperent;
            case DEVICE_PHONE: return R.drawable.type_smartphone;
            case DEVICE_TABLET: return R.drawable.type_tablet;
            case DEVICE_LAPTOP: return R.drawable.type_laptop;
            case DEVICE_DESKTOP: return R.drawable.type_desktop;
            case DEVICE_TV: return R.drawable.type_tv;
            case DEVICE_ROUTER: return R.drawable.type_router;
            default:throw new IllegalStateException("Unknown:"+deviceType);
        }
    }*/
}
