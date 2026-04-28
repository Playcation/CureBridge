package com.example.contentservice.news.filter;

import java.util.Set;

public class KeywordFilter {

  public static final Set<String> EXCLUDED_KEYWORDS = Set.of(
      "의료", "리가",
      "회장", "대표", "원장", "교수", "이사장", "부회장", "사장", "센터장", "병원장",
      "기자", "뉴스", "사진", "제공",
      "이번", "지난", "오늘", "내일",
      "관련", "통해", "위한", "등", "및"
  );

}