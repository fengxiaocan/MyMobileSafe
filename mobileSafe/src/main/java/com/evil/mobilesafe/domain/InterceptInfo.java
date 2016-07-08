package com.evil.mobilesafe.domain;

import java.io.Serializable;

public class InterceptInfo implements Serializable{
	public String phone;
	public int type;	//拦截模式
	public String desc;
	@Override
	public String toString() {
		return "InterceptInfo [phone=" + phone + ", type=" + type + ", desc="
				+ desc + "]";
	}
	@Override
	public boolean equals(Object obj) {
		return this.phone.equals(((InterceptInfo)obj).phone);
	}

	
	
}
