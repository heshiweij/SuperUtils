package sample;

import cn.ifavor.utils.db.Column;
import cn.ifavor.utils.db.Table;

@Table("student")
public class Filter {
	@Column("id")
	private int id;
	
	@Column("name")
	private String name;
	
	@Column("addr")
	private String addr;
	
	@Column("age")
	private int age;
	
	@Column("email")
	private String email;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddr() {
		return addr;
	}

	public void setAddr(String addr) {
		this.addr = addr;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@Override
	public String toString() {
		return "Filter [id=" + id + ", name=" + name + ", addr=" + addr
				+ ", age=" + age + "]";
	}

	public String testGit(){
		
	}
	
}
