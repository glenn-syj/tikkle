import { MypageMemberResponse } from "@/types";
import { handleApiRequest } from "../client";

// 마이페이지
export const fetchMypageMember = async () => {
  return handleApiRequest<MypageMemberResponse, "get">("/member", "get");
};

export const logOut = async () => {
  return handleApiRequest<void, "get">("/logout", "get");
};
