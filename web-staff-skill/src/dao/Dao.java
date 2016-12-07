package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import dto.Religion;
import dto.School;
import dto.Skill;
import dto.Staff;

public class Dao {
	static Connection conn = null;
	static PreparedStatement pstmt;
	static ResultSet rs;
	
	//��� ��� ��������
	public static ArrayList<Skill> selectSkill(){
		Skill skill = null;
		ArrayList<Skill> skillList = null;
		try{
			conn = DBUtil.getConnection();
			System.out.println("conn : "+conn);
			pstmt = conn.prepareStatement("select no,name from skill");
			rs = pstmt.executeQuery();
			skillList = new ArrayList<Skill>();
			while(rs.next()){
				skill = new Skill(rs.getInt("no"),rs.getString("name"));
				System.out.println(skill);
				skillList.add(skill);
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			close();
		}
		return skillList;
	}
	
	//��� �з����� ��������
	public static ArrayList<School> selectSchool(){
		School school = null;
		ArrayList<School> schoolList = null;
		try{
			conn = DBUtil.getConnection();
			System.out.println("conn : "+conn);
			pstmt = conn.prepareStatement("select no,graduate from school");
			rs = pstmt.executeQuery();
			schoolList = new ArrayList<School>();
			while(rs.next()){
				school = new School(rs.getInt("no"),rs.getString("graduate"));
				System.out.println(school);
				schoolList.add(school);
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			close();
		}
		return schoolList;
	}
	
	//��� �������� ��������
	public static ArrayList<Religion> selectReligion(){
		Religion religion = null;
		ArrayList<Religion> religionlList = null;
		try{
			conn = DBUtil.getConnection();
			System.out.println("conn : "+conn);
			pstmt = conn.prepareStatement("select no,name from religion");
			rs = pstmt.executeQuery();
			religionlList = new ArrayList<Religion>();
			while(rs.next()){
				religion = new Religion(rs.getInt("no"),rs.getString("name"));
				System.out.println(religion);
				religionlList.add(religion);
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			close();
		}
		return religionlList;
	}
	
	//ȸ������ �Է��ϱ� 
	public static int insertStaff(Staff staff, int[] skillNo){
		int rowCount = 0;
		System.out.println("insertStaff() Dao.java");
		try{
			conn = DBUtil.getConnection();
			System.out.println("skillNo.size()"+skillNo.length);
			
			//staff ���̺� �Է�
			pstmt = conn.prepareStatement("insert into staff (name,sn, GRADUATEDAY, SCHOOLNO, RELIGIONNO) values(?,?,?,?,?)");
			pstmt.setString(1, staff.getName());
			pstmt.setString(2, staff.getSn());
			pstmt.setString(3, staff.getGraduateday());
			pstmt.setInt(4, staff.getSchool().getNo());
			pstmt.setInt(5, staff.getReligion().getNo());
			pstmt.executeUpdate();
			System.out.println("staff �Է¼���");
			
			//staff ���̺� ��� �Էµ� no ������
			pstmt = conn.prepareStatement("select no from staff where sn=?");
			pstmt.setString(1, staff.getSn());
			rs = pstmt.executeQuery();
			System.out.println("rs:"+rs);
			int staffNo = 0;
			if(rs.next()){
				staffNo = rs.getInt("no");
				System.out.println("staffNo:"+staffNo);
			}
			
			//������ no������ staffskill ���̺� �� �Է�
			pstmt = conn.prepareStatement("insert into staffskill (staffno,skillno) values(?,?)");
			for(int i = 0 ; i<skillNo.length;i++){
				System.out.println("�ݺ���"+i);
				pstmt.setInt(1, staffNo);
				pstmt.setInt(2, skillNo[i]);
				pstmt.executeUpdate();
			}
			System.out.println("staffskill �Է¼���");
		} catch(Exception e){
			e.printStackTrace();
		}finally{
			close();
		}
		
		return rowCount;
		
	}
	
	
	
	//��ü�����ϱ�
	public static void close(){
		if (rs != null)	try { rs.close();} catch (SQLException ex) {}
		if (pstmt != null) try { pstmt.close();	} catch (SQLException ex) {}
		if (conn != null) try {	conn.close(); } catch (SQLException ex) {}
	}
}
