# 게시판 애플리케이션

created on 2021. 7. 18

---

## 기능

### Board 게시물

- Command
- [x] 생성 (로그인 / 비로그인)
- [x] 삭제 (로그인 / 비로그인)
- [x] 수정 (로그인 / 비로그인)
- [ ] 조회 수 업데이트 (상세 조회 시)


- Query
- [x] ID로 상세 조회
  - 게시물 정보, 댓글 조회
- [x] 리스트 조회 (페이지)
  - 게시물 제목, 작성자, 생성 일, 댓글 수
- [x] 검색 (페이지)
  - 게시물 제목, 작성자, 생성 일, 댓글 수

### Comment 댓글

- Command
- [x] 생성 (로그인 / 비로그인)
- [x] 삭제 (로그인 / 비로그인)
- [x] 수정 (로그인 / 비로그인)


- Query
- [x] 게시물 ID로 조회 (페이지)

### Member 유저

- Command
- [x] 생성 (로그인 / 비로그인)
- [x] 삭제 (로그인 / 비로그인)
- [x] 수정 (로그인 / 비로그인)
  

- Query
- [x] ID로 상세 조회
  - Member 정보, 작성한 게시물
- [x] Username으로 검색 (페이지)