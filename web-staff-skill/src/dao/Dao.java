package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import dto.Religion;
import dto.School;
import dto.Search;
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
	
	public static ArrayList<Staff> allStaff(){
		System.out.println("Dao.java allStaff() ����");
		String sql = "";
		Staff staff;
		ArrayList<Staff> staffList = null;
		PreparedStatement skillPstmt;
		ResultSet skillRs;
		ArrayList<Skill> skillList = null;
		sql = "select st.`no`,st.name,substring(sn, 8,1)as sn,re.name as religionno , sc.graduate as schoolno,graduateday from staff st inner join religion re on st.religionno = re.`no` inner join school sc on st.schoolno = sc.`no` order by st.name asc";
		try{
			conn = DBUtil.getConnection();
			
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();
			staffList = new ArrayList<Staff>();
			while(rs.next()){
				staff = new Staff();
				System.out.println(staff);
				staff.setName(rs.getString("name"));
				
				//�ֹι�ȣ �߸� ���ڰ����ͼ� ��� 2�γ����� �������� '��' �ȶ�������'��'�� ����
				int genderNum = Integer.parseInt(rs.getString("sn"));
				if(genderNum%2!=0){
					staff.setSn("��");
				}else{
					staff.setSn("��");
				}
				
				staff.setGraduateday(rs.getString("graduateday"));
				School school = new School();
				school.setGraduate(rs.getString("schoolno"));;
				staff.setSchool(school);
				Religion religion = new Religion();
				religion.setName(rs.getString("religionno"));
				staff.setReligion(religion);
				
				//skill �������� ������
				//skill �� �� staff ���� ������ �̹Ƿ� ArrayList�� ��� ������������ staff�� ��´�.
				skillPstmt = conn.prepareStatement("select  staffskill.staffno , staffskill.skillno, skill.name from staffskill inner join skill on staffskill.skillno = skill.`no`  where staffskill.staffno=? ; ");
				skillPstmt.setInt(1, rs.getInt("no"));	//���� staff�� no ������ skill ���̺�� staffskill ���̺��� ������ �����´�
				skillRs = skillPstmt.executeQuery();
				skillList = new ArrayList<Skill>();
				
				//skill �� ������� �ϳ��� �ְ� �װ͵��� ArrayList�� ��´�.
				while(skillRs.next()){
					Skill skill = new Skill();
					skill.setName(skillRs.getString("name"));
					skillList.add(skill);
				}
				
				//���� ArrayList �� staff �� ����
				staff.setSkillList(skillList);
				staffList.add(staff);
				System.out.println(staff);
			}
			System.out.println("��ü����Ʈ ������");
		} catch(Exception e){
			e.printStackTrace();
		}finally{
			close();
		}
		return staffList;
	}
	
	public static ArrayList<Staff> searchStaff(Search search){
		System.out.println("Dao.java searchStaff() ����");
		String sql = "";
		Staff staff;
		ArrayList<Staff> staffList = null;
		PreparedStatement skillPstmt;
		ResultSet skillRs;
		ArrayList<Skill> skillList = null;
		if(search.getName()==""&&
				search.getGender()==null&&
				search.getSchoolNo()==null&&
				search.getSkillNo()==null&&
				search.getGraduateDayEnd()==""&&
				search.getGraduateDayStart()==""){
			sql = "select st.`no`,st.name,substring(sn, 8,1)as sn,re.name as religionno , sc.graduate as schoolno,graduateday from staff st inner join religion re on st.religionno = re.`no` inner join school sc on st.schoolno = sc.`no` order by st.name asc";
			try{
				conn = DBUtil.getConnection();
				
				pstmt = conn.prepareStatement(sql);
				rs = pstmt.executeQuery();
				staffList = new ArrayList<Staff>();
				while(rs.next()){
					staff = new Staff();
					System.out.println(staff);
					staff.setName(rs.getString("name"));
					
					//�ֹι�ȣ �߸� ���ڰ����ͼ� ��� 2�γ����� �������� '��' �ȶ�������'��'�� ����
					int genderNum = Integer.parseInt(rs.getString("sn"));
					if(genderNum%2!=0){
						staff.setSn("��");
					}else{
						staff.setSn("��");
					}
					
					staff.setGraduateday(rs.getString("graduateday"));
					School school = new School();
					school.setGraduate(rs.getString("schoolno"));;
					staff.setSchool(school);
					Religion religion = new Religion();
					religion.setName(rs.getString("religionno"));
					staff.setReligion(religion);
					
					//skill �������� ������
					//skill �� �� staff ���� ������ �̹Ƿ� ArrayList�� ��� ������������ staff�� ��´�.
					skillPstmt = conn.prepareStatement("select  staffskill.staffno , staffskill.skillno, skill.name from staffskill inner join skill on staffskill.skillno = skill.`no`  where staffskill.staffno=? ; ");
					skillPstmt.setInt(1, rs.getInt("no"));	//���� staff�� no ������ skill ���̺�� staffskill ���̺��� ������ �����´�
					skillRs = skillPstmt.executeQuery();
					skillList = new ArrayList<Skill>();
					
					//skill �� ������� �ϳ��� �ְ� �װ͵��� ArrayList�� ��´�.
					while(skillRs.next()){
						Skill skill = new Skill();
						skill.setName(skillRs.getString("name"));
						skillList.add(skill);
					}
					
					//���� ArrayList �� staff �� ����
					staff.setSkillList(skillList);
					staffList.add(staff);
					System.out.println(staff);
				}
				System.out.println("��ü����Ʈ ������");
			} catch(Exception e){
				e.printStackTrace();
			}finally{
				close();
			}
		}
		return staffList;
	}
	
	
	public static ArrayList<Staff> getList(String sql){
		Staff staff;
		ArrayList<Staff> staffList = null;
		PreparedStatement skillPstmt;
		ResultSet skillRs;
		ArrayList<Skill> skillList = null;
		
		try{
			conn = DBUtil.getConnection();
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();
			staffList = new ArrayList<Staff>();
			while(rs.next()){
				staff = new Staff();
				System.out.println(staff);
				staff.setName(rs.getString("name"));
				
				//�ֹι�ȣ �߸� ���ڰ����ͼ� ��� 2�γ����� �������� '��' �ȶ�������'��'�� ����
				int genderNum = Integer.parseInt(rs.getString("sn"));
				if(genderNum%2!=0){
					staff.setSn("��");
				}else{
					staff.setSn("��");
				}
				
				staff.setGraduateday(rs.getString("graduateday"));
				School school = new School();
				school.setGraduate(rs.getString("schoolno"));;
				staff.setSchool(school);
				Religion religion = new Religion();
				religion.setName(rs.getString("religionno"));
				staff.setReligion(religion);
				
				//skill �������� ������
				//skill �� �� staff ���� ������ �̹Ƿ� ArrayList�� ��� ������������ staff�� ��´�.
				skillPstmt = conn.prepareStatement("select  staffskill.staffno , staffskill.skillno, skill.name from staffskill inner join skill on staffskill.skillno = skill.`no`  where staffskill.staffno=? ; ");
				skillPstmt.setInt(1, rs.getInt("no"));	//���� staff�� no ������ skill ���̺�� staffskill ���̺��� ������ �����´�
				skillRs = skillPstmt.executeQuery();
				skillList = new ArrayList<Skill>();
				
				//skill �� ������� �ϳ��� �ְ� �װ͵��� ArrayList�� ��´�.
				while(skillRs.next()){
					Skill skill = new Skill();
					skill.setName(skillRs.getString("name"));
					skillList.add(skill);
				}
				
				//���� ArrayList �� staff �� ����
				staff.setSkillList(skillList);
				staffList.add(staff);
				System.out.println(staff);
			}
			System.out.println("��ü����Ʈ ������");
		} catch(Exception e){
			e.printStackTrace();
		}finally{
			close();
		}
		return staffList;
	}
	
	
	//��ü�����ϱ�
	public static void close(){
		if (rs != null)	try { rs.close();} catch (SQLException ex) {}
		if (pstmt != null) try { pstmt.close();	} catch (SQLException ex) {}
		if (conn != null) try {	conn.close(); } catch (SQLException ex) {}
	}
}
