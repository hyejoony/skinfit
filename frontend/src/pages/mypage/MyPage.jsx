import "./MyPage.scss";
import ToggleButton from "../../components/common/ToggleButton";
import {
  useMyPageInfo,
  useTop3Data,
  useMyCosmetics,
  useMyIngredients,
  useReviews,
} from "../../stores/Mypage";
import { useState } from "react";
import ReviewItem from "../../components/review/ReviewItem";
import PwCheckPopUp from "../../components/auth/PwCheckPopUp";
import { Link } from "react-router-dom";

function MyPage() {
  //1. 스토어 데이터 불러오기
  // 마이페이지 유저 정보 불러오기
  const { myinfos } = useMyPageInfo();

  // 성분 top3 데이터
  const { top3Data } = useTop3Data();

  // 내가 등록한 화장품 데이터
  const { myMatchedCosData, myUnMatchedCosData } = useMyCosmetics();

  // 성분 데이터
  const { myIngredientsData } = useMyIngredients();

  // 리뷰 데이터
  const { myReviews, likedReviews } = useReviews();

  //2. 액션 감지
  // 내가 등록한 화장품 토글 버튼 감지
  const [isCosmeticClicked, setIsCosmeticClicked] = useState("맞는 화장품");
  const cosmeticHandler = (text) => {
    setIsCosmeticClicked(text);
    console.log("화장품 토글:", text);
  };

  // 내가 등록한 성분 토글 버튼 감지
  const [isIngredientClicked, setIsIngredientClicked] = useState("맞는 성분");
  const ingredientHandler = (text) => {
    setIsIngredientClicked(isIngredientClicked);
    console.log("마이페이지 성분 데이터 조회", text);
  };

  // 리뷰 토글 버튼 감지
  const [isReviewClicked, setReviewIsClicked] = useState("내가 좋아요한 리뷰");
  const reviewHandler = (text) => {
    setReviewIsClicked(text);
    console.log("마이페이지에서 리뷰감지", text); // 업데이트된 값을 직접 출력
  };

  // 내 정보수정 비밀번호 확인 팝업 관리
  const [isMyInfoOpen, setyMyInfoOpen] = useState(false);
  const handlePwPopup = () => {
    setyMyInfoOpen(!isMyInfoOpen);
  };

  // 회원탈퇴 비밀번호 확인 팝업 관리
  const [isWithdrawOpen, setWithdrawIsOpen] = useState(false);
  const handleWithdrawPopup = () => {
    setWithdrawIsOpen(!isWithdrawOpen);
  };

  return (
    <div className="wrapper">
      {/* 유저 닉네임, 피부타입 정보 */}
      <div className="edit-user-info">
        <span className="user-name">{myinfos?.nickname}</span>
        <span className="user-name2">님</span>
        <div className="user-skin-type">
          {myinfos &&
            myinfos.skinTypeId.map((skinType, index) => (
              <span key={index}>#{skinType} </span>
            ))}
        </div>
        <div>
          <button className="edit-info-btn" onClick={handlePwPopup}>
            내 정보 수정하기
          </button>
          {isMyInfoOpen && (
            <PwCheckPopUp onClose={handlePwPopup} state="myinfo" />
          )}
        </div>
      </div>

      {/* 랭킹 3 정보 */}
      <div className="top3-box">
        <div className="title">
          <span>skinfit이 찾은 </span>
          <span className="color-text">나와 맞지 않는 성분 TOP 3</span>
        </div>
        <hr />
        <div className="ranking-text">
          {Array.isArray(top3Data) &&
            top3Data.map((top3, index) => (
              <div key={index}>
                <span className="rank-num">{index + 1}</span>
                <span className="ingredient-name">{top3.ingredientName}</span>
              </div>
            ))}
        </div>
        <div className="ingredient-detail-btn">
          <Link to="/ingredients" className="detail-btn">
            성분 자세히 보기
          </Link>
        </div>
      </div>

      {/* 내가 등록한 화장품 정보 */}
      <div className="cosmetic-wrapper">
        <h2>내가 등록한 화장품</h2>
        <div className="set-position">
          <ToggleButton
            text1="맞는 화장품"
            text2="맞지 않는 화장품"
            handler={cosmeticHandler}
          />
          <button className="edit-del-btn">수정</button>
        </div>

        {/* 조건부 렌더링 및 map() */}
        {(isCosmeticClicked === "화장품"
          ? myMatchedCosData
          : myUnMatchedCosData
        )?.map((cos) => (
          <div key={cos.id} className="cosmetic-item-wrapper">
            <div className="cosmetic-item">
              <span className="cosmetic-name">{cos.cosmeticName}</span>
              <img src="" alt="" />
            </div>
            <hr />
          </div>
        ))}
      </div>

      {/* 내가 등록한 성분 정보 */}
      {/* <div className="my-ingredient-box">
        <h2>내가 등록한 성분</h2>
        <div className="set-position">
          <ToggleButton
            text1="맞는 성분"
            text2="맞지 않는 성분"
            handler={ingredientHandler}
          />
          <button className="edit-del-btn">수정</button>
        </div>
        {(isIngredientClicked === "맞는 성분"
          ? myMatchedIngData
          : myUnMatchedIngData
        )?.map((ing) => (
          <div key={ing.id}>
            <div className="ingredient-list">
              <div className="ingredient-name">{ing.name}</div>
              <div className="ewg-level">{ing.ewgLevel}</div>
            </div>
            <hr />
          </div>
        ))}
      </div> */}

      {/* 리뷰 목록 */}
      <div className="review-wrapper">
        <h2>리뷰 목록</h2>
        {/* props로 함수 전달 */}
        <ToggleButton
          text1="내가 좋아요한 리뷰"
          text2="내가 작성한 리뷰"
          handler={reviewHandler}
        />

        {/* 리뷰 데이터를 하나씩 전달 */}
        {isReviewClicked === "내가 좋아요한 리뷰"
          ? likedReviews?.map((review) => (
              <ReviewItem key={review.reviewId} review={review} />
            ))
          : myReviews?.map((review) => (
              <ReviewItem key={review.reviewId} review={review} />
            ))}
      </div>

      {/* 회원탈퇴 */}
      <hr className="hr-line" />
      <div>
        <p className="signout" onClick={handleWithdrawPopup}>회원탈퇴</p>
        {isWithdrawOpen && (
          <PwCheckPopUp onClose={handleWithdrawPopup} state="withdraw" />
        )}
      </div>
    </div>
  );
}

export default MyPage;
