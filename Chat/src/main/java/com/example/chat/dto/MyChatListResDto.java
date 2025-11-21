package com.example.chat.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class MyChatListResDto {
    private Long roomId;
    private String roomName;
    private String isGroupChat;
    private Long unReadCount;
}
