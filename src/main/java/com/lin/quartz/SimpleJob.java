package com.lin.quartz;

import java.text.SimpleDateFormat;
import java.util.Date;

public class SimpleJob {

	public void execute() {
		System.out.println(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
				.format(new Date()) + "执行了SimpleJob");
	}

}