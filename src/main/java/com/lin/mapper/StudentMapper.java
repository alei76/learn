package com.lin.mapper;

import java.util.List;

import com.lin.model.StudentEntity;

public interface StudentMapper<T> {
	public T getStudent(int id);

	public List<T> getStudentAll();

	public void insertStudent(T entity);

	public void updateStudent(T entity);

	public void deleteStudent(int studentId);

}
