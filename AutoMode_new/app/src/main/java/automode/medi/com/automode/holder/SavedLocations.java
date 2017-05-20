package automode.medi.com.automode.holder;

/**
 * Created by ist on 23/4/17.
 */

public class SavedLocations {

    private  Integer uid;
    private  String address;
    private  Double lattitude;
    private  Double longitude;
    private  Integer mode;


    public Integer getUid() {
        return uid;
    }

    public void setUid(Integer uid) {
        this.uid = uid;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Double getLattitude() {
        return lattitude;
    }

    public void setLattitude(Double lattitude) {
        this.lattitude = lattitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Integer getMode() {
        return mode;
    }

    public void setMode(Integer mode) {
        this.mode = mode;
    }

    @Override
    public String toString() {
        return "SavedLocations{" +
                "uid=" + uid +
                ", address='" + address + '\'' +
                ", lattitude=" + lattitude +
                ", longitude=" + longitude +
                ", mode=" + mode +
                '}';
    }
}
