package com.mjc.groupware.pos.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.mjc.groupware.pos.entity.Pos;

public interface PosRepository extends JpaRepository<Pos, Long> {
	
	boolean existsByPosName(String posName);
	
	@Query("SELECT COALESCE(MAX(p.posOrder), 0) FROM Pos p")
	int findMaxOrder();
	
	@Query("SELECT p FROM Pos p ORDER BY p.posOrder ASC")
	List<Pos> findAllOrderByPosOrderAsc();
	
	@Query("SELECT p FROM Pos p WHERE p.posOrder = (SELECT MAX(p2.posOrder) FROM Pos p2)")
	Optional<Pos> findTopByOrderByPosOrderDesc();
	
}
