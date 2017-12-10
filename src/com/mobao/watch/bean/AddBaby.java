package com.mobao.watch.bean;

public class AddBaby {
	
	String imei;
	String portrait;//头像
	String relate;
    String name;
    String age;
    String height;
    String babyphone;
    String sex;
    String weight;
    String steplen;
    String birthday;
    
    public AddBaby(){
    	
    }

	public AddBaby(String imei,String portrait, String relate, String name, String age,
			String height,String babyphone) {
		super();
		this.imei = imei;
		this.portrait = portrait;
		this.relate = relate;
		this.name = name;
		this.age = age;
		this.height = height;
		this.babyphone = babyphone;
	}
	
	public AddBaby(String imei, String portrait, String relate, String name,
			String age, String height, String babyphone, String sex,
			String weight, String steplen,String birthday) {
		super();
		this.imei = imei;
		this.portrait = portrait;
		this.relate = relate;
		this.name = name;
		this.age = age;
		this.height = height;
		this.babyphone = babyphone;
		this.sex = sex;
		this.weight = weight;
		this.steplen = steplen;
		this.birthday = birthday;
	}


	public String getBirthday() {
		return birthday;
	}

	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}

	public String getBabyphone() {
		return babyphone;
	}

	public void setBabyphone(String babyphone) {
		this.babyphone = babyphone;
	}

	public String getImei() {
		return imei;
	}

	public void setImei(String imei) {
		this.imei = imei;
	}

	public String getPortrait() {
		return portrait;
	}

	public void setPortrait(String portrait) {
		this.portrait = portrait;
	}

	public String getRelate() {
		return relate;
	}

	public void setRelate(String relate) {
		this.relate = relate;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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

	public String getSteplen() {
		return steplen;
	}

	public void setSteplen(String steplen) {
		this.steplen = steplen;
	}
    
	
    
}
