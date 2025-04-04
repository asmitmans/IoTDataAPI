package com.futuro.iotdataapi.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.futuro.iotdataapi.entity.Menu;

public interface MenuRepository extends JpaRepository<Menu, Integer> {

	@Query(value = """
			    select m.* from menu_role mr
			    inner join user_roles ur on ur.role_id = mr.id_role
			    inner join menu m on m.id_menu = mr.id_menu
			    inner join users u on u.id = ur.user_id
			    where u.username = :username
			    order by id_father,id_menu
			""", nativeQuery = true)
	List<Menu> getMenusByUsername(@Param("username") String username);

	List<Menu> findAllByOrderByIdMenu();
}
