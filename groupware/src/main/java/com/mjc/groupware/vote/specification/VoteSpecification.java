package com.mjc.groupware.vote.specification;

import org.springframework.data.jpa.domain.Specification;

import com.mjc.groupware.vote.entity.Vote;

public class VoteSpecification {

    public static Specification<Vote> voteTitleContains(String keyword) {
        return (root, query, cb) -> {
            if (keyword == null || keyword.trim().isEmpty()) return null;
            return cb.like(cb.lower(root.get("voteTitle")), "%" + keyword.toLowerCase() + "%");
        };
    }
}