package com.lin.model;


public class StudentEntity {
	private static final long serialVersionUID = 3096154202413606831L;
	private String studentBirthday;
	private String studentID;
	private String studentName;
	private String studentSex;

	public String getStudentID() {
		return studentID;
	}

	public String getStudentName() {
		return studentName;
	}

	public String getStudentSex() {
		return studentSex;
	}

	public String getStudentBirthday() {
		return studentBirthday;
	}

	public void setStudentBirthday(String studentBirthday) {
		this.studentBirthday = studentBirthday;
	}

	public void setStudentID(String studentID) {
		this.studentID = studentID;
	}

	public void setStudentName(String studentName) {
		this.studentName = studentName;
	}

	public void setStudentSex(String studentSex) {
		this.studentSex = studentSex;
	}

	public StudentEntity(String studentBirthday, String studentName,
			String studentSex) {
		super();
		this.studentBirthday = studentBirthday;
		this.studentName = studentName;
		this.studentSex = studentSex;
	}

	public StudentEntity(String studentBirthday, String studentID,
			String studentName, String studentSex) {
		super();
		this.studentBirthday = studentBirthday;
		this.studentID = studentID;
		this.studentName = studentName;
		this.studentSex = studentSex;
	}
	
}
