package com.cz.cvut.fel.instumentalshop.service.chat;

import com.cz.cvut.fel.instumentalshop.domain.chat.ChatMessage;
import com.cz.cvut.fel.instumentalshop.domain.chat.ChatRoom;
import com.cz.cvut.fel.instumentalshop.dto.chat.ChatRoomDto;

import java.util.List;

/**
 * Služba pro správu chatovacích místností a zpráv.
 */
public interface ChatService {

    /**
     * Vrátí seznam chatovacích místností, kde je uživatel účastníkem.
     *
     * @param userId ID uživatele
     * @return seznam entit ChatRoom
     */
    List<ChatRoomDto> getUserRooms(Long userId);

    /**
     * Otevře novou chatovací místnost mezi dvěma uživateli,
     * nebo vrátí již existující.
     *
     * @param userId1 ID prvního uživatele (ten, kdo volá)
     * @param userId2 ID druhého uživatele
     * @return entita ChatRoom
     */
    ChatRoomDto openRoom(Long userId1, Long userId2);

    /**
     * Vrátí historii zpráv v dané místnosti, seřazenou podle času odeslání vzestupně.
     *
     * @param roomId ID chatovací místnosti
     * @return seznam entit ChatMessage
     */
    List<ChatMessage> getMessages(Long roomId);

    /**
     * Uloží novou zprávu do místnosti a aktualizuje informaci o poslední zprávě v místnosti.
     *
     * @param roomId   ID místnosti
     * @param senderId ID odesílatele (uživatele)
     * @param content  text zprávy
     * @return právě uložená entita ChatMessage
     */
    ChatMessage saveMessage(Long roomId, Long senderId, String content);
}
