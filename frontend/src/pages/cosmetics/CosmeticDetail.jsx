import { useState } from "react";
import { Link, useParams } from "react-router-dom";
import { useQuery } from "@tanstack/react-query";

import axios from "../../api/axiosInstance.js";
import "./CosmeticDetail.scss";
import AllIngrePopup from "../../components/cosmetics/AllIngrePopup";
import Header from "../../components/common/Header";
import Button from "../../components/common/Button";
import CosmeticInfo from "../../components/cosmetics/CosmeticInfo";
import NavBar from "../../components/common/NavBar";
import ReviewItem from "../../components/review/ReviewItem";
import { useNavigate } from "react-router-dom";

function CosmeticDetail() {
  // 먼저 파라미터 가져오기
  const { cosmeticId } = useParams();

  // 전성분 팝업창 제어
  const [isPopupOpen, setIsPopupOpen] = useState(false);

  // 내 피부 맞춤 리뷰 토글
  const [isOn, setIsOn] = useState(true);

  const handleToggle = () => {
    setIsOn(!isOn);
  };

  // 리뷰 정렬
  const [sortOrder, setSortOrder] = useState("likes");
  const [page, setPage] = useState(1);

  const navigate = useNavigate();

  // 헤더 뒤로가기
  const handleBack = () => navigate(-1);

  // 정렬
  const handleSort = (order) => {
    setSortOrder(order);
  };

  // 리뷰 요청 함수
  const fetchReviews = async ({ cosmeticId, sort, page, custom }) => {
    const response = await axios.get(`cosmetics/${cosmeticId}/reviews`, {
      params: {
        sort,
        page,
        limit: 10,
        custom: custom ? "true" : "false",
      },
    });
    console.log('내피부맞춤',custom, sort)
    return response.data.reviews;
  };

  // 화장품 정보 요청 함수
  const fetchCosmeticDetails = async (cosmeticId) => {
    const response = await axios.get(`cosmetic/${cosmeticId}`);
    return response.data;
  };

  // 리뷰 요청
  const { data: reviews } = useQuery({
    queryKey: ["reviews", cosmeticId, sortOrder, page, isOn],
    queryFn: () =>
      fetchReviews({
        cosmeticId,
        sort: sortOrder,
        page,
        limit: 10,
        custom: isOn,
      }),
  });

  // 화장품 정보 요청
  const { data: cosmeticData, isLoading } = useQuery({
    queryKey: ["cosmetic", cosmeticId],
    queryFn: () => fetchCosmeticDetails(cosmeticId),
  });
  // #region 전성분 보기 팝업창 함수
  // 전성분 팝업 열기
  const openPopup = () => {
    setIsPopupOpen(true);
  };

  // 전성분 팝업 닫기
  const closePopup = () => {
    setIsPopupOpen(false);
  };
  // #endregion

  return (
    <div className="cosmetic-detail">
      <Header title="상세 정보" onBack={handleBack} />

      {/* 화장품 정보 */}
      <CosmeticInfo cosmeticData={cosmeticData} />

      {/* 전성분 보기 버튼 */}
      <Button text="전성분 보기" color="white" onClick={openPopup} />
      {isPopupOpen && (
        <AllIngrePopup closePopup={closePopup} cosmeticId={cosmeticId} />
      )}

      {/* 리뷰 */}
      <div className="reviews">
        <p className="title">리뷰</p>

        {/* 내 피부 맞춤 리뷰 */}
        <div className="skin-type-review">
          <div
            className={`toggle-btn ${isOn ? "on" : "off"}`}
            onClick={handleToggle}
          >
            <div className="slider"></div>
          </div>
          <p>내 피부 맞춤 리뷰</p>
        </div>

        {/* 리뷰 컨트롤(정렬, 글 작성) */}
        <div className="review-controls">
          <div className="sort-order-btn">
            <button
              className={`likes ${sortOrder === "likes" ? "active" : ""}`}
              onClick={() => handleSort("likes")}
            >
              좋아요순
            </button>
            <button
              className={`latest ${sortOrder === "latest" ? "active" : ""}`}
              onClick={() => handleSort("latest")}
            >
              최신순
            </button>
          </div>
          {!isLoading && cosmeticData && (
            <Link to={"review"} className="write-btn">
              작성하기
            </Link>
          )}
        </div>

        {/* 리뷰 리스트 */}
        <div className="review-list">
          {reviews?.map((review, idx) => (
            <ReviewItem key={idx} review={review} reviewType="generalReviews" />
          ))}
        </div>
      </div>

      {/* 네브바 */}
      <NavBar />
    </div>
  );
}

export default CosmeticDetail;
