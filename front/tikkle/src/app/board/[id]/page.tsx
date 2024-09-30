"use client";

import dynamic from "next/dynamic";
import "@toast-ui/editor/dist/toastui-editor.css";
import Badge from "@/components/badge/Badge";
import Chips from "@/components/chips/Chips";
import { usePathname } from "next/navigation";
import { useDeleteBoard, useFetchBoardDetail } from "@/hooks/board";
import { useState } from "react";
import Dropbox from "@/components/drop-down/Dropbox";
import { useRouter } from "next/navigation";
import { useQueryClient } from "@tanstack/react-query";
// 동적으로 Viewer 컴포넌트를 로드
const Viewer = dynamic(
  () => import("@toast-ui/react-editor").then((mod) => mod.Viewer),
  { ssr: false },
);

export default function BoardDetail() {
  const [isDropboxOpen, setIsDropboxOpen] = useState(false); // Dropbox 열림 상태 관리

  const toggleDropbox = () => {
    setIsDropboxOpen(!isDropboxOpen); // 토글 기능 구현
  };
  const router = useRouter();
  const pathname = usePathname();
  const queryClient = useQueryClient(); // react-query의 QueryClient를 가져옴
  // URL에서 boardId 추출
  const boardId = pathname.split("/").pop()!; // 경로의 마지막 부분이 boardId
  console.log(boardId);
  const { mutate: deleteBoard } = useDeleteBoard(boardId);

  const handleItemClick = (item: string) => {
    if (item === "수정하기") {
      console.log("Clicked item:", item);
    } else {
      console.log("Clicked item:", item);
      deleteBoard(boardId, {
        onSuccess: () => {
          queryClient.invalidateQueries({ queryKey: ["Boards"] });
          queryClient.invalidateQueries({ queryKey: ["board", boardId] });

          router.push("/board"); // 성공 후 리다이렉트
        },
      });
    }
  };
  // useFetchBoardDetail 훅을 통해 boardId로 데이터를 가져옴
  const { data: board, isLoading, error } = useFetchBoardDetail(boardId);

  // 콘솔 로그로 실제 데이터를 확인
  console.log("API Response:", board);

  // 로딩 상태 처리
  if (isLoading) {
    return <div>Loading...</div>;
  }

  // 에러 상태 처리
  if (error) {
    return <div>Error: {error.message}</div>;
  }

  // 데이터가 없을 경우 처리
  if (!board) {
    return <div>No board data available.</div>;
  }

  // 가져온 데이터를 board로 사용

  return (
    <>
      <div className="text-40 font-bold text-teal900">SSAFY의 티끌</div>
      <div className="flex flex-1 flex-col gap-12 self-stretch rounded-12 border border-warmGray200 px-40 py-36">
        {/* 제목 영역 */}
        <div className="flex items-center gap-20">
          <Badge size="l" color="teal">
            {board.status}
          </Badge>
          <div className="flex flex-1 text-28 font-bold">{board.title}</div>
          <div className="relative flex flex-col">
            {/* relative 추가 */}
            <span
              className="material-symbols-outlined cursor-pointer"
              onClick={toggleDropbox} // 클릭 시 드롭박스 열림 상태 토글
            >
              more_horiz
            </span>
            {isDropboxOpen && ( // 드롭박스 열림 상태에 따라 표시
              <div className="absolute right-0 top-full z-10">
                <Dropbox
                  onClick={handleItemClick}
                  items={["수정하기", "삭제하기"]}
                />
              </div>
            )}
          </div>
        </div>

        {/* 작성자, 작성일 */}
        <div className="flex items-center gap-20 pb-10">
          <div className="font-semibold text-warmGray500">{board.memberId}</div>
          <div className="text-warmGray400">
            {board.createdAt
              ? new Date(board.createdAt).toLocaleString()
              : "N/A"}
          </div>
        </div>

        {/* 카테고리, 예상 시간 */}
        <div className="flex items-center gap-20">
          <Chips size="l" variant="primary" design="fill">
            {board.category ?? "null"}
          </Chips>
          <div className="w-1 self-stretch bg-warmGray200"></div>
          <div className="flex items-center justify-center gap-10">
            <div>예상 시간</div>
            <div className="font-semibold text-teal-500">{board.time}</div>
            <div>시간</div>
          </div>
        </div>

        {/* 내용 */}
        <div className="flex flex-1 self-stretch px-10 py-20 text-17 leading-7">
          <Viewer width="100%" initialValue={board.content} />
        </div>
      </div>
    </>
  );
}
