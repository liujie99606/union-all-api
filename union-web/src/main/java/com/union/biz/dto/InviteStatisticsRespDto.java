package com.union.biz.dto;

import lombok.Data;

import java.util.List;

@Data
public class InviteStatisticsRespDto {

    //总共邀请人数
    private Integer totalInviteNum;

    //有效邀请人数
    private Integer effectiveInviteNum;

    //邀请用户列表
    private List<InviteUser> inviteUserList;

    @Data
    public static class InviteUser {
        //昵称
        private String nickName;

        //是否有效
        private Boolean effective;

    }


}
