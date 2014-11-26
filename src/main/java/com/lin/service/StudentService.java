package com.lin.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lin.mapper.StudentMapper;
import com.lin.model.StudentEntity;

@Service
public class StudentService {
	@Autowired
	private StudentMapper<StudentEntity> dao;

	public StudentEntity getStudentById(int id) {
		return dao.getStudent(id);
	}

	public List<StudentEntity> getStudentAll() {
		return dao.getStudentAll();
	}

	public void insert(StudentEntity entity) {
		dao.insertStudent(entity);
	}

	public void update(StudentEntity entity) {
		dao.updateStudent(entity);
	}

	public void delete(int id) {
		dao.deleteStudent(id);
	}
}
