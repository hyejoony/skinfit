import "./OcrScanner.scss";
import ImageUpload from "../../components/common/ImageUpload";
import Button from "../../components/common/Button";
import { useRef, useState } from "react";
import axios from "../../api/axiosInstance";
import { useMutation } from "@tanstack/react-query";
import Header from '../../components/common/Header.jsx'


export default function OcrScanner() {
  // ocr 사진 데이터
  const [ocrData, setOcrData] = useState({
    cosmeticBrand: "",
    cosmeticName: "",
    categoryId: "",
    ingredientImage: [],
    cosmeticVolume: "",
  });

  // ocr 이미지 필수제출 관리
  const [imageError, setImageError] = useState(false);

  //카테고리 타입
  const allTypes = [
    "로션",
    "스킨",
    "에센스",
    "크림",
    "클렌징",
    "바디",
    "선케어",
  ];

  // 카테고리 관리
  const handleType = (type) => {
    setOcrData((prevData) => ({
      categoryId: prevData.categoryId === type ? "" : type,
    }));
  };

  // 데이터 POST 요청
  const uploadOcr = async (ocr) => {
    const formData = new FormData();
    formData.append("cosmeticBrand", ocr.cosmeticBrand);
    formData.append("cosmeticName", ocr.cosmeticName);
    formData.append("cosmeticVolume", ocr.cosmeticVolume);
    formData.append("categoryId", ocr.categoryId);
    ocr.ingredientImage.forEach((file) =>
      formData.append("ingredientImage", file)
    );
    console.log("ocr 사진 upload 한다");

    return axios.post(`/ocr`, formData);
  };

  const mutation = useMutation({
    mutationFn: uploadOcr,
    onSuccess: () => {
      alert("사진이 등록되었어요.");
      setOcrData({
        cosmeticBrand: "",
        cosmeticName: "",
        categoryId: "",
        ingredientImage: [],
        cosmeticVolume: "",
      }); //초기화
    },
    onError: () => {
      alert("사진 등록에 실패했어요.");
    },
  });

  //미입력 에러메시지 관리리
  const brandNameErrorRef = useRef(null);
  const cosmeticNameErrorRef = useRef(null);
  const capacityErrorRef = useRef(null);
  const categoriesErrorRef = useRef(null);
  // const imagesErrorRef = useRef(null)

  // 폼 제출출
  const handleSubmit = (e) => {
    e.preventDefault();

    let hasError = false;
    const fields = [
      { name: "cosmeticBrand", ref: brandNameErrorRef },
      { name: "cosmeticName", ref: cosmeticNameErrorRef },
      { name: "cosmeticVolume", ref: capacityErrorRef },
      { name: "categoryId", ref: categoriesErrorRef },
      // { name: "images", ref: imagesErrorRef },
    ];

    fields.forEach((field) => {
      const value = ocrData[field.name];
      if (!value) {
        field.ref.current.classList.add("error");
        hasError = true;
      } else {
        field.ref.current.classList.remove("error");
      }
    });

    if (ocrData.images?.length === 0) {
      setImageError(true);
      hasError = true;
    } else {
      setImageError(false);
    }

    if (!hasError) {
      mutation.mutate(ocrData);
    }
  };

  return (
    <>
  <Header title='성분 스캐너'/>
    <div className="ocr-register-form">
      <div className="input-wrapper">
        <p className="input-title">상품명</p>
        <input id="name" type="text" placeholder="상품명을 입력해주세요" />
        <p className="error-msg" ref={cosmeticNameErrorRef}>
          상품명 입력은 필수예요
        </p>
      </div>
      <div className="input-wrapper">
        <p className="input-title">브랜드명</p>
        <input id="name" type="text" placeholder="브랜드명을 입력해주세요" />
        <p className="error-msg" ref={brandNameErrorRef}>
          브랜드명 입력은 필수예요
        </p>
      </div>

      <div className="category-group">
        <label>카테고리</label>
        <div className="skintype-options">
          {allTypes.map((type) => (
            <button
              key={type}
              type="button"
              className={`skin-btn ${
                ocrData.categories === type ? "selected" : ""
              }`}
              onClick={() => handleType(type)}
            >
              {type}
            </button>
          ))}
        </div>
        <p className="error-msg" ref={categoriesErrorRef}>
          카테고리 선택은 필수예요{" "}
        </p>
      </div>

      <div className="input-wrapper">
        <p className="input-title">상품용량(mL)</p>
        <input id="name" type="text" placeholder="상품용량을 입력해주세요" />
        <p className="error-msg" ref={capacityErrorRef}>
          상품용량 입력은 필수예요
        </p>
      </div>

      {/* 리뷰 사진 등록 */}
      <ImageUpload
        images={ocrData.images}
        setImages={setOcrData}
        maxImages={3}
        dataType="ocr"
        onError={setImageError}
        error={imageError}
      />

      <Button
        text="등록하기"
        color="pink"
        type="submit"
        onSubmit={handleSubmit}
      />
    </div>
    </>
  );
}
