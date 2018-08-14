package com.alany.u2.service;

import java.util.List;
/**
 * Created by alany on 2018/7/27.
 */
public interface KeywordReplyService {

    void importData();

    List<String> getReplyList(String comment);

    String getDefaultReply();
}