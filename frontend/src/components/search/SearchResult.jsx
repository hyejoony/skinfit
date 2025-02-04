//SearchResult : 검색 결과(검색 결과 없어요, 검색 결과 리스트(searchItem) -> if뮨 처리)
import "./SearchResult.scss"
import { useEffect, useState } from "react";

function SearchResult({  }) {

  //검색 액션 여부 -> 조건부 랜더링링
  const [hasResults, setHasResults] = useState(false);

  // for문으로 실시간 연관검색어 목록 반환
  useEffect(() => {
    const checkResults = async () => {
      const results = await fetchSearchResults();
      setHasResults(results.length > 0);
    };

    checkResults();
  }, []);

  // 검색어 입력 글자수 제한
  // 임의로 25글자 이상일 때 .. 처리
  const truncateText = (text, maxLength) => {
    if (text.length > maxLength) {
      return text.slice(0, maxLength) + ".."
    }
    return text
  }

  return (
    <>
    {hasResults? ( 
    <div className='search-wrapper2'>
      <div className="search-result">
        {/* 검색결과받기 */}
        <p>{truncateText( , 25)}</p>
      </div>
      <hr />
    </div>
    ) : (
    <div className="wrong-search">
      <h2>검색 결과가 없어요</h2>
      <p>철자를 확인하거나 다른 키워드로 검색해주세요</p>
      <p className="ptag-2">등록되지 않은 화장품이라면?</p>
      <button className="register-btn">사진 찍어 화장품 등록하기</button>
    </div>
    )}
    </>
  )
}

export default SearchResult
