package com.mobao.watch.bean;

public class Baby {

	private String babyimei;
	private String babyname;
	private String addtiem;
	private String portrait;
	private String age;
	private String height;
	private String relate;
	private String value;
	private String babyphone;
	private String sex="boy";
	private String weight;
	private String steepLong;
	private String birthday;

	public Baby() {

	}

	public Baby(String babyimei, String babyname) {
		super();
		this.babyimei = babyimei;
		this.babyname = babyname;
	}
	public String getBabyphone() {
		return babyphone;
	}

	public void setBabyphone(String babyphone) {
		this.babyphone = babyphone;
	}

	public Baby(String babyimei, String babyname, String addtiem,
			String portrait) {
		super();
		this.babyimei = babyimei;
		this.babyname = babyname;
		this.addtiem = addtiem;
		this.portrait = portrait;
	}
	
	public Baby(String babyname, String portrait, String age, String height,
			String relate,String value) {
		super();
		this.babyname = babyname;
		this.portrait = portrait;
		this.age = age;
		this.height = height;
		this.relate = relate;
		this.value = value;
	}
	
	public Baby(String babyimei, String babyname, String addtiem,
			String portrait, String age, String height, String relate,
			String value, String babyphone, String sex, String weight,
			String steepLong,String birthday) {
		super();
		this.babyimei = babyimei;
		this.babyname = babyname;
		this.addtiem = addtiem;
		this.portrait = portrait;
		this.age = age;
		this.height = height;
		this.relate = relate;
		this.value = value;
		this.babyphone = babyphone;
		this.sex = sex;
		this.weight = weight;
		this.steepLong = steepLong;
		this.birthday = birthday;
	}
	
	

	public String getBirthday() {
		return birthday;
	}

	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}

	public String getAddtiem() {
		return addtiem;
	}

	public void setAddtiem(String addtiem) {
		this.addtiem = addtiem;
	}

	public String getPortrait() {
		return portrait;
	}

	public void setPortrait(String portrait) {
		this.portrait = portrait;
	}

	public String getBabyimei() {
		return babyimei;
	}

	public void setBabyimei(String babyimei) {
		this.babyimei = babyimei;
	}

	public String getBabyname() {
		return babyname;
	}

	public void setBabyname(String babyname) {
		this.babyname = babyname;
	}

	public String getAge() {
		return age;
	}

	public void setAge(String age) {
		this.age = age;
	}

	public String getHeight() {
		return height;
	}

	public void setHeight(String height) {
		this.height = height;
	}

	public String getRelate() {
		return relate;
	}

	public void setRelate(String relate) {
		this.relate = relate;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getWeight() {
		return weight;
	}

	public void setWeight(String weight) {
		this.weight = weight;
	}

	public String getSteepLong() {
		return steepLong;
	}

	public void setSteepLong(String steepLong) {
		this.steepLong = steepLong;
	}
	
	

}
