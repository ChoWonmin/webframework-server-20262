### 도커 mysql 컨테이너 실행문

``` bash
docker compose up -d --wait
```

openssl rand -base64 32



DB 설계
 - 채팅방 (채팅방 이름, 채팅방 설명, 생성시간, 가입자 수, 좋아요 수, 비밀번호)
 - 가입 명부 (id, user_id, chatroom_id, 시간) 중긴테이블
 - 메세지 (user_id 작성자, chatroom_id, 시간, 내용)
 - 좋아요 여부 (id, user_id, chatroom_id)

