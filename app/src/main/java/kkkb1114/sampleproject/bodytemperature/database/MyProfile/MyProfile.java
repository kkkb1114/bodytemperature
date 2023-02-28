package kkkb1114.sampleproject.bodytemperature.database.MyProfile;

public class MyProfile {
    public String name;
    public int gender;
    public String birthDate;
    public String weight;
    public String purpose;

    public MyProfile(String name, int gender, String birthDate, String weight, String purpose){
        this.name = name;
        this.gender = gender;
        this.birthDate = birthDate;
        this.weight = weight;
        this.purpose = purpose;
    }

    @Override
    public String toString() {
        return "MyProfile{" +
                "name='" + name + '\'' +
                ", gender=" + gender +
                ", birthDate='" + birthDate + '\'' +
                ", weight='" + weight + '\'' +
                ", purpose='" + purpose + '\'' +
                '}';
    }
}
