package com.jasonmsoft.wechat_encrpt;

/**
 * Created by cdmaji1 on 2016/2/3.
 */
public class AffectMsgObj {
    public String mMsgline = "";
    public String mTimeStamp = "";
    public int mRole;
    public static final int ROLE_MYSELF = 1;
    public static final int ROLE_PEER = 2;
    public String mAvatarDesc = "";
}
