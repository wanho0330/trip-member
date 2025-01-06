package com.trip.user.controller;

import com.trip.user.dto.*;
import com.trip.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/apiv1/user")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;


    /**
     * 사용자를 생성합니다.
     *
     * @param req {@code CreateUser.Req} 객체에 담긴 이메일, 이름, 비밀번호
     * @return {@code CreateUser.Res} 객체에 담긴 생성된 사용자 정보를 포함하는 ResponseEntity
     */
    @PostMapping
    public ResponseEntity<CreateUser.Res> createUser(@RequestBody CreateUser.Req req) {
        logger.info("UserController-createUser-Create user : {}", req);
        User user = userService.createUser(CreateUser.ReqToUser(req), req.getPassword());

        CreateUser.Res res = CreateUser.Res.builder()
                .user(user)
                .build();

        logger.info("UserController-createUser-complete");
        return ResponseEntity.status(HttpStatus.CREATED).body(res);
    }

    /**
     * 제공된 사용자 인덱스로 사용자 정보를 조회합니다.
     *
     * @param idx 사용자 고유 식별자 정보
     * @return {@code GetUserByIdx.Res} 객체에 담긴 조회된 사용자 정보를 포함하는 ResponseEntity
     */
    @GetMapping(value = "/{idx}")
    public ResponseEntity<GetUserByIdx.Res> getUserByIdx(@PathVariable("idx") Long idx) {
        logger.info("UserController-getUserByIdx-getUserByIdx : {}", idx);
        User user = userService.getUserByIdx(idx);

        GetUserByIdx.Res res = GetUserByIdx.Res.builder()
                .user(user)
                .build();

        logger.info("UserController-getUserByIdx-complete");
        return ResponseEntity.ok(res);
    }


    /**
     * 비밀번호를 제외한 사용자의 정보를 변경합니다.
     *
     * @param idx 사용자 고유 식별자 정보
     * @param req {@code UpdateUser.Req} 객체에 담긴 이메일, 이름, 비밀번호
     * @return {@code UpdateUser.Res} 객체에 담긴 업데이트된 사용자 정보를 포함하는 ResponseEntity
     */
    @PutMapping("/{idx}")
    public ResponseEntity<UpdateUser.Res> updateUser(@PathVariable("idx") Long idx, @RequestBody UpdateUser.Req req) {
        logger.info("UserController-updateUser-Update user : {}", req);
        User user = userService.updateUser(idx, req.getUser());

        UpdateUser.Res res = UpdateUser.Res.builder()
                .user(user)
                .build();

        logger.info("UserController-updateUser-complete");
        return ResponseEntity.ok(res);
    }

    /**
     * 사용자의 비밀번호를 변경합니다.
     *
     * @param idx 사용자 고유 식별자 정보
     * @param req {@code UpdatePassword.Req} 객체에 담긴 현재 비밀번호, 변경할 비밀번호
     * @return {@code UpdatePassword.Res} 객체에 담긴 업데이트된 사용자 정보를 포함하는 ResponseEntity
     */
    @PutMapping("/{idx}/password")
    public ResponseEntity<UpdatePassword.Res> updatePassword(@PathVariable("idx") Long idx, @RequestBody UpdatePassword.Req req) {
        logger.info("UserController-updatePassword-Update user : {}", req);
        User user = userService.updatePassword(idx, req.getOldPassword(), req.getNewPassword());

        UpdatePassword.Res res = UpdatePassword.Res.builder()
                .user(user)
                .build();

        logger.info("UserController-updatePassword-complete");
        return ResponseEntity.ok(res);
    }


    /**
     * 사용자의 상태를 삭제로 변경합니다.
     *
     * @param idx 사용자 고유 식별자 정보
     * @return 내용이 없는 ResponseEntity
     */
    @DeleteMapping("/{idx}")
    public ResponseEntity<?> deleteUser(@PathVariable("idx") Long idx) {
        logger.info("UserController-deleteUser-Delete user : {}", idx);
        userService.deleteUser(idx);

        logger.info("UserController-deleteUser-complete");
        return ResponseEntity.noContent().build();
    }


    /**
     * 삭제 상태가 아닌 모든 사용자를 조회합니다.
     *
     * @param cursor 조회된 사용자 중 마지막 인덱스
     * @param pageSize 한번에 조회할 사용자의 갯수
     * @return {@code GetAllUsers.Res} 객체에 담긴 사용자들의 정보를 포함하는 ResponseEntity
     */
    @GetMapping
    public ResponseEntity<GetAllUsers.Res> getAllUsers(@RequestParam("cursor") Long cursor, @RequestParam("pageSize") int pageSize) {
        logger.info("UserController-getAllUsers-getAllUsers cursor : {}", cursor);

        GetAllUsers.Res res = GetAllUsers.Res.builder()
                .users(userService.getAllUsers(cursor, pageSize))
                .build();

        logger.info("UserController-getAllUsers-complete");
        return ResponseEntity.ok(res);
    }
}

