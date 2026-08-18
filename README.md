# CommunityProject 社區管理 App

一款以 **Java + Android SDK** 開發的社區（社群）管理行動應用程式，提供社區公告、活動報名、景點與特產介紹、會員審核與權限管理等功能。App 端不含資料庫，所有資料透過 HTTP + JSON 與後端 REST API 溝通。

---

## 目錄

- [專案概觀](#專案概觀)
- [使用技術](#使用技術)
- [開發環境需求](#開發環境需求)
- [快速開始](#快速開始)
- [專案結構](#專案結構)
- [角色與權限設計](#角色與權限設計)
- [功能模組說明](#功能模組說明)
- [畫面流程](#畫面流程)
- [後端 API 一覽](#後端-api-一覽)
- [資料傳輸格式](#資料傳輸格式)
- [本機狀態儲存（Session）](#本機狀態儲存session)
- [圖片處理流程](#圖片處理流程)
- [程式碼慣例與命名說明](#程式碼慣例與命名說明)
- [已知問題與注意事項](#已知問題與注意事項)

---

## 專案概觀

| 項目 | 內容 |
| --- | --- |
| 專案名稱 | CommunityProject |
| Application ID | `com.example.communityproject` |
| 版本 | versionCode 1 / versionName 1.0 |
| 開發語言 | Java 8 |
| 架構 | 傳統 Activity + Adapter（無 MVVM／無 Repository 層） |
| 程式規模 | `app/src/main/java` 下 59 個 Java 檔、約 10,012 行 |
| 資源規模 | 55 個 layout、81 個 drawable |
| 註冊 Activity 數 | 25 個（含 `<activity>` 宣告） |
| 後端 | 外部 REST API（**不在本 repo 內**），共呼叫 48 個端點 |

本專案為單一 Gradle module（`:app`）的 Android 應用程式，沒有 Kotlin、沒有多模組、沒有 DI 框架。每個畫面（Activity／Fragment）自行組裝 JSON 請求、送出、解析回應，再交給對應的 RecyclerView Adapter 顯示。

---

## 使用技術

### 建置工具

| 技術 | 版本 | 用途 |
| --- | --- | --- |
| Android Gradle Plugin | 4.1.2 | 建置系統 |
| Gradle Wrapper | 6.5 | 建置工具版本 |
| compileSdk / targetSdk | 30 (Android 11) | 編譯與目標 API |
| minSdk | 24 (Android 7.0 Nougat) | 最低支援版本 |
| buildTools | 30.0.2 | 建置工具鏈 |
| Java | 1.8（source / target compatibility） | 語言版本 |
| View Binding | 已於 `buildFeatures` 啟用 | （實際程式仍以 `findViewById` 取得元件） |

### AndroidX / Google 元件

| 函式庫 | 版本 | 用途 |
| --- | --- | --- |
| `androidx.appcompat:appcompat` | 1.3.1 | AppCompatActivity 基底 |
| `com.google.android.material:material` | 1.4.0 | Material Design 元件（TabLayout、SwitchCompat 等） |
| `androidx.constraintlayout:constraintlayout` | 2.1.0 | 版面配置 |
| `androidx.recyclerview:recyclerview` | 1.2.1 | 清單顯示（全專案主力） |
| `androidx.recyclerview:recyclerview-selection` | 1.1.0 | 清單多選 |
| `androidx.cardview:cardview` | 1.0.0 | 卡片式版面 |
| `androidx.viewpager2:viewpager2` | 1.0.0 | 分頁切換（會員審核頁） |
| `androidx.swiperefreshlayout:swiperefreshlayout` | 1.1.0 | 下拉重新整理 |
| `androidx.navigation:navigation-fragment` / `-ui` | 2.3.5 | 導覽元件（相依已引入） |
| `androidx.legacy:legacy-support-v4` | 1.0.0 | 相容支援 |
| `androidx.wear:wear` / `wearable` | 1.0.0 / 2.6.0 | Wear 相依（專案樣板殘留） |

### 第三方函式庫

| 函式庫 | 版本 | 用途 |
| --- | --- | --- |
| `com.android.volley:volley` | 1.2.0 | **所有網路請求**（`JsonObjectRequest`） |
| `com.github.bumptech.glide:glide` | 4.12.0 | 遠端圖片載入與快取 |
| `de.hdodenhof:circleimageview` | 3.1.0 | 圓形頭像 |
| `com.github.denzcoskun:ImageSlideshow` | 0.0.6 | 圖片輪播（`ImageSlider` + `SlideModel`） |
| `com.github.chrisbanes:PhotoView` | 2.1.3 | 圖片縮放檢視（用於 `image_dialog.xml`） |
| `com.karumi:dexter` | 6.2.1 | **已宣告但未使用**，權限實際以 `ActivityCompat.requestPermissions()` 處理 |
| `com.github.smarteist:autoimageslider` | 1.4.0 | **已宣告但未使用** |

執行期權限（`READ_EXTERNAL_STORAGE`）於 `RegisterActivity`、`CreateCommunityActivity`、`PasswordUpdate_Activity` 三處以 `ActivityCompat.requestPermissions()` 直接請求。

### 測試

| 函式庫 | 版本 |
| --- | --- |
| JUnit | 4.13.2 |
| AndroidX Test ext:junit | 1.1.3 |
| Espresso Core | 3.4.0 |

> 目前僅有 Android Studio 產生的樣板測試 `app/src/test/.../ExampleUnitTest.java`（`assertEquals(4, 2 + 2)`）與 `app/src/androidTest/.../ExampleInstrumentedTest.java`，**尚未撰寫實際測試案例**。

---

## 開發環境需求

- Android Studio（建議使用能相容 AGP 4.1.2 的版本，例如 Arctic Fox 或更早）
- JDK 8
- Android SDK Platform 30 + Build Tools 30.0.2
- 可連線的後端 API 服務

---

## 快速開始

1. **取得專案**

   ```bash
   git clone git@github.com:t0976306551/CommunityProject.git
   cd CommunityProject
   ```

2. **設定 SDK 路徑**

   `local.properties` 已被 `.gitignore` 排除（內含本機路徑，不應進版控）。請自行建立：

   ```properties
   sdk.dir=C\:\\Users\\<你的使用者名稱>\\AppData\\Local\\Android\\Sdk
   ```

   使用 Android Studio 開啟專案時通常會自動產生此檔。

3. **設定後端位址**

   所有 API 位址集中在 `app/src/main/java/com/example/communityproject/UrlSetting.java`：

   ```java
   public class UrlSetting {
       Context context;
       String address = "120.119.77.79";
       String testaddress = "http://192.168.0.43/usr/public/";

       public String getUrl(){
           return testaddress;
   //        return "https://lab0726.at.tw/usr/";
       }
   }
   ```

   `getUrl()` 目前回傳 `testaddress`（區域網路測試機）。**部署或連線正式站前，請將此處改為正式 API 位址**（註解中保留了 `https://lab0726.at.tw/usr/`）。網址結尾必須帶 `/`，因為程式各處是以 `getUrl() + "user/login"` 的方式串接。

4. **建置與安裝**

   ```bash
   ./gradlew assembleDebug
   ./gradlew installDebug
   ```

   或直接在 Android Studio 按下 Run。

---

## 專案結構

```
CommunityProject/
├─ app/
│  ├─ build.gradle                  # module 建置設定與相依
│  ├─ proguard-rules.pro
│  └─ src/
│     ├─ main/
│     │  ├─ AndroidManifest.xml
│     │  ├─ java/com/example/communityproject/
│     │  │  ├─ MainActivity.java            # 登入後主選單
│     │  │  ├─ SessionManager.java          # 一般會員登入狀態
│     │  │  ├─ UrlSetting.java              # API base url（唯一設定點）
│     │  │  ├─ CallingDialog.java           # 共用成功對話框（目前所有引用皆被註解）
│     │  │  ├─ CreateCommunityActivity.java # 社區建立申請
│     │  │  ├─ IntroduceActivity.java       # 社區介紹
│     │  │  ├─ IntorduceAdapter.java
│     │  │  ├─ IntorduceCardViewData.java
│     │  │  ├─ ManagementActivity.java      # 空的樣板 Activity（未實作）
│     │  │  ├─ PasswordUpdate_Activity.java # 個人資料／密碼修改
│     │  │  ├─ LoginAndRegister/            # 登入、註冊、社區選擇與建立
│     │  │  ├─ Post/                        # 公告／貼文與留言
│     │  │  ├─ Acyivity/                    # 活動（資料夾名稱為誤拼，見下方說明）
│     │  │  ├─ Attraction/                  # 社區景點
│     │  │  ├─ Staple/                      # 社區特產
│     │  │  └─ UserCheck/                   # 會員審核與權限管理
│     │  └─ res/
│     │     ├─ layout/                      # 55 個版面
│     │     ├─ drawable/, drawable-v24/     # 81 個圖檔與 shape
│     │     ├─ anim/                        # 對話框進出場動畫
│     │     ├─ menu/, mipmap-*/
│     │     └─ values/, values-night/, values-round/
│     ├─ androidTest/                       # 樣板 instrumented test
│     └─ test/
├─ build.gradle                     # 專案層級建置設定
├─ settings.gradle                  # 僅 include ':app'
├─ gradle/wrapper/                  # Gradle 6.5 wrapper
├─ gradlew / gradlew.bat
└─ gradle.properties
```

### 各套件（package）職責

| 套件 | 主要類別 | 職責 |
| --- | --- | --- |
| （root） | `MainActivity`、`SessionManager`、`UrlSetting`、`CallingDialog` | 主選單、登入狀態、API 位址、成功對話框 |
| `LoginAndRegister` | `LoginActivity`、`RegisterActivity`、`SelectCommunityActivity`、`ManagerCommunityActivity`、`CreateCommunityAdapter`、`CommunityAdapter`、`ManagerSessionLogin` | 登入、註冊、選擇社區、系統管理員後台 |
| `Post` | `PostActivity`、`insert_post_Activity`、`UpdatePostActivity`、`PostAdapter`、`ReplyActivity`、`ReplyAdapter`、`ReplyMessageActivity` | 公告／貼文 CRUD 與留言、回覆 |
| `Acyivity` | `Acyivity_Activity`、`insert_activity_Activity`、`ActivityAdapter`、`Activity_record_Activity`、`RecordAdapter`、`SelectPeopleActivity`、`SelectAdapter` | 活動列表、建立、報名、歷史紀錄、報名名單 |
| `Attraction` | `AttractionActivity`、`insert_attraction_Activity`、`attraction_page_Activity`、`AttractionAdapter`、`ImageAdapter` | 社區景點列表、新增、詳細頁、編輯 |
| `Staple` | `StapleActivity`、`insert_staple_Activity`、`staple_page_Activity`、`StapleAdapter` | 社區特產列表、新增、詳細頁、編輯 |
| `UserCheck` | `userCheckActivity`、`userCheckFragment`、`blockadeFragment`、`FragmentAdapter`、`UserCheckAdapter`、`UserAuthorityAdapter` | 註冊審核、現有會員管理、權限調整 |

---

## 角色與權限設計

系統共有三個層級：

### 1. 系統管理員（Manager）

- 以同一個 `LoginActivity` 登入，後端回傳 `success == "5"` 時判定為系統管理員。
- 登入狀態存於 **`ManagerSessionLogin`**（SharedPreferences 檔名 `MANAGER_LOGIN`），與一般會員的 session 完全分離。
- 登入後導向 `ManagerCommunityActivity`，功能為**審核各社區的建立申請**（`user/selectCreateCommunity` 取得申請清單，`user/successCreateCommunity` 核准）。

### 2. 社區內權限（依 `a_id` / `a_name`）

- 一般會員登入後（`success == "1"`），session 內存有 `A_ID`（權限代號）與 `AUTHORITY`（權限名稱字串）。
- 權限名稱與可選清單由後端提供（`user/getAuthority`、`user/getOtherAuthority`），**並非寫死在 App 內**。
- App 內硬編碼的兩項判斷：

  | 判斷位置 | 條件 | 效果 |
  | --- | --- | --- |
  | `MainActivity` | `AUTHORITY.equals("一般會員")` | 隱藏「會員審核」入口 |
  | `Acyivity_Activity`、`AttractionActivity`、`StapleActivity`、`attraction_page_Activity`、`staple_page_Activity` | `A_ID.equals("3")` | 隱藏新增／編輯按鈕 |

  換言之 `a_id == "3"` 對應一般會員；其餘代號為具備管理權的幹部角色。

### 3. 會員審核狀態

`LoginActivity` 依後端回傳的 `success` 值決定行為：

| success | 意義 | App 行為 |
| --- | --- | --- |
| `"1"` | 一般會員登入成功 | 建立 session → `MainActivity` |
| `"2"` | 帳號尚未通過審核 | 顯示「帳號尚未通過審核」 |
| `"3"` | 審核失敗 | 顯示「此帳號審核失敗請重新申請」 |
| `"5"` | 系統管理員登入成功 | 建立 manager session → `ManagerCommunityActivity` |
| 其他 | 帳密錯誤 | 顯示「帳號或密碼輸入錯誤」 |

---

## 功能模組說明

### 登入與註冊（`LoginAndRegister`）

- **`LoginActivity`**（Launcher Activity）
  以帳號密碼呼叫 `user/login`。啟動時先檢查兩組 SharedPreferences，若已登入則直接跳轉，達成「保持登入」。
- **`RegisterActivity`**
  註冊時送出 `account`、`password`、`name`、`sex`、`community`、`image`（Base64），需選擇要加入的社區。註冊後帳號進入待審核狀態。
- **`SelectCommunityActivity`**
  以 `user/getCommunity` 取得可加入的社區清單，供註冊時挑選。
- **`CreateCommunityActivity`**
  向系統管理員提出建立新社區的申請，送出社區名稱、地址、電話，以及管理者的帳號、密碼、姓名、性別與大頭照。
- **`ManagerCommunityActivity` + `CreateCommunityAdapter`**
  系統管理員後台：列出所有待審核的社區申請，點擊可查看申請人詳細資料並核准建立。

### 主選單（`MainActivity`）

登入後的入口，以 CardView 呈現九個功能磚：社區介紹（目前預設 `GONE` 隱藏）、公告、活動、活動紀錄、景點、特產、會員審核、修改資料、登出。支援 SwipeRefresh 重新讀取 session 顯示資料，`onResume` 時亦會刷新（讓修改頭像／姓名後立即反映）。

### 公告／貼文（`Post`）

- **`PostActivity`**：以 `post/` 取得社區公告列表，另提供 `post/search` 關鍵字搜尋。
- **`insert_post_Activity`**：新增公告，可附帶**最多 5 張圖片**（超過會提示並自動裁切至 5 張），並以 `reply_check` 開關控制此篇是否開放留言。
- **`UpdatePostActivity`**：以 `post/load` 載入原內容後，透過 `post/edit` 更新標題與內文。
- **`PostAdapter`**：清單顯示與 `post/delete` 刪除。
- **`ReplyActivity` / `ReplyAdapter`**：貼文留言列表（`reply/load`）、新增留言（`reply/create`）、刪除留言（`reply/delete`）。
- **`ReplyMessageActivity`**：針對某則留言的「回覆訊息」串（`reply/loadReplyMessage`、`reply/insertReplyMessage`）。

### 活動（`Acyivity`）

> 資料夾與部分類別名稱拼字為 `Acyivity`（Activity 的誤拼），為專案原始命名，請勿誤認為筆誤而任意更名，會影響 `AndroidManifest.xml` 中的宣告。

- **`Acyivity_Activity`**：進行中的活動列表（`activity/get`，`type = "1"`）。
- **`Activity_record_Activity`**：已結束的活動紀錄（`activity/get`，`type = "2"`）。
- **`insert_activity_Activity`**（433 行）：建立活動，包含名稱、內容、起訖日期與時間（DatePicker / TimePicker）、人數上限與活動照片。
- **`ActivityAdapter`**（868 行，全專案最大檔）：活動卡片的完整互動邏輯 —— 載入詳細（`activity/load`）、查詢報名人數（`activity/getTotal`）、報名（`activity/join`）、取消報名（`activity/cancel`）、編輯（`activity/edit`）與刪除（`activity/delete`）。
- **`RecordAdapter`**（391 行）：歷史活動卡片，載入與刪除。
- **`SelectPeopleActivity` / `SelectAdapter`**：查看活動報名名單（`activity/select`），點擊成員可查看其詳細資料（`user/load`）。

### 景點（`Attraction`）與特產（`Staple`）

兩個模組的結構幾乎相同（列表 → 詳細頁 → 編輯／刪除）：

| 功能 | 景點 | 特產 |
| --- | --- | --- |
| 列表 | `attraction/` | `staple/` |
| 新增 | `attraction/create` | `staple/create` |
| 詳細載入 | `attraction/load` | `staple/load` |
| 欄位更新 | `attraction/updateAttractionData` | `staple/updateStapleData` |
| 刪除 | `attraction/delete` | `staple/delete` |

詳細頁採用「單欄位更新」設計：以 `type` 指定要修改的欄位名稱，`updateData` 帶入新值，送往同一支更新 API。圖片以輪播元件呈現，並可點擊放大檢視。

### 會員審核與權限（`UserCheck`）

`userCheckActivity` 以 `TabLayout` + `ViewPager2` 分為兩頁：

| 分頁 | Fragment | 功能 |
| --- | --- | --- |
| 註冊審核 | `userCheckFragment` | 列出待審核的註冊申請（`user/userCheck`），可核准（`user/success`）或退回／刪除（`user/delete`） |
| 目前會員 | `blockadeFragment` | 列出社區現有會員（`user/getUser`），透過 `UserAuthorityAdapter` 以 Spinner 調整權限（`user/getAuthority`、`user/getOtherAuthority`、`user/updateAuthority`），成員資料以 `user/load` 載入 |

### 個人資料（`PasswordUpdate_Activity`）

單一頁面處理多種更新，皆送往 `user/updateData`，以 `type` 區分：

| `type` | 動作 | 附帶欄位 |
| --- | --- | --- |
| `updateName` | 修改姓名 | `updateName` |
| `updatePassword` | 修改密碼 | `old_password`、`new_password` |
| `updateImage` | 更換頭像 | `updataImage`（Base64，清空時傳 `""`） |

另以 `user/setUpdateData` 取得目前資料。更新成功後同步呼叫 `SessionManager.update()` 更新本機 session，讓主畫面立即反映。

### 社區介紹（`IntroduceActivity`）

- `user/getCommunityintorduce`：取得社區簡介的三個區塊 —— `context`（社區沿革）、`develop`（發展）、`vision`（願景），點擊各區塊以 `intorduce_dialog` 彈窗顯示全文。
- `user/getCommunityMessager`：取得社區幹部清單（`m_id`、`m_name`、`a_name`、`image`、`authority_id`），以 `IntorduceAdapter` 呈現。

**目前在 `MainActivity` 中被設為 `View.GONE`，入口尚未開放。**

---

## 畫面流程

```
LoginActivity（Launcher）
├─ 已有 MANAGER_LOGIN ──▶ ManagerCommunityActivity ──▶ 審核社區建立申請
├─ 已有 LOGIN ─────────▶ MainActivity
├─ 註冊 ───────────────▶ RegisterActivity ──▶ SelectCommunityActivity（挑選社區）
└─ 申請建立社區 ───────▶ CreateCommunityActivity

MainActivity（主選單）
├─ 公告      ─▶ PostActivity ─▶ insert_post_Activity / UpdatePostActivity
│                            └▶ ReplyActivity ─▶ ReplyMessageActivity
├─ 活動      ─▶ Acyivity_Activity ─▶ insert_activity_Activity
│                                 └▶ SelectPeopleActivity（報名名單）
├─ 活動紀錄  ─▶ Activity_record_Activity
├─ 景點      ─▶ AttractionActivity ─▶ attraction_page_Activity / insert_attraction_Activity
├─ 特產      ─▶ StapleActivity ─▶ staple_page_Activity / insert_staple_Activity
├─ 會員審核  ─▶ userCheckActivity（註冊審核 / 目前會員 兩個分頁）
├─ 修改資料  ─▶ PasswordUpdate_Activity
├─ 社區介紹  ─▶ IntroduceActivity（目前隱藏）
└─ 登出      ─▶ 清除 session ─▶ LoginActivity
```

---

## 後端 API 一覽

所有端點皆以 `UrlSetting.getUrl()` 為前綴，一律使用 **HTTP POST + JSON**（含僅為查詢用途的端點）。回應為 JSON 物件，慣例上包含 `success` 狀態碼與 `data` 陣列。

### `user/` — 使用者與社區（18 個）

| 端點 | 用途 | 呼叫位置 |
| --- | --- | --- |
| `user/login` | 登入 | `LoginActivity` |
| `user/register` | 註冊 | `RegisterActivity` |
| `user/getCommunity` | 取得可加入的社區清單 | `SelectCommunityActivity` |
| `user/createCommunity` | 申請建立社區 | `CreateCommunityActivity` |
| `user/selectCreateCommunity` | 取得社區建立申請清單 | `ManagerCommunityActivity`、`CreateCommunityAdapter` |
| `user/successCreateCommunity` | 核准社區建立 | `CreateCommunityAdapter` |
| `user/userCheck` | 取得待審核會員 | `userCheckFragment` |
| `user/success` | 核准會員 | `UserCheckAdapter` |
| `user/delete` | 刪除／退回會員 | `UserCheckAdapter` |
| `user/getUser` | 取得社區現有會員 | `blockadeFragment` |
| `user/load` | 取得單一會員詳細資料 | `UserAuthorityAdapter`、`SelectAdapter` |
| `user/getAuthority` | 取得該會員目前權限 | `UserAuthorityAdapter` |
| `user/getOtherAuthority` | 取得可指派的其他權限 | `UserAuthorityAdapter` |
| `user/updateAuthority` | 更新會員權限 | `UserAuthorityAdapter` |
| `user/setUpdateData` | 取得個人資料以供編輯 | `PasswordUpdate_Activity` |
| `user/updateData` | 更新姓名／密碼／頭像 | `PasswordUpdate_Activity` |
| `user/getCommunityintorduce` | 取得社區介紹 | `IntroduceActivity` |
| `user/getCommunityMessager` | 取得社區幹部／聯絡人 | `IntroduceActivity` |

### `post/` 與 `reply/` — 公告與留言（11 個）

| 端點 | 用途 | 呼叫位置 |
| --- | --- | --- |
| `post/` | 公告列表 | `PostActivity` |
| `post/search` | 公告搜尋 | `PostActivity` |
| `post/create` | 新增公告 | `insert_post_Activity` |
| `post/load` | 載入單篇公告 | `UpdatePostActivity` |
| `post/edit` | 編輯公告 | `UpdatePostActivity` |
| `post/delete` | 刪除公告 | `PostAdapter` |
| `reply/load` | 留言列表 | `ReplyActivity` |
| `reply/create` | 新增留言 | `ReplyActivity` |
| `reply/delete` | 刪除留言 | `ReplyAdapter` |
| `reply/loadReplyMessage` | 回覆訊息列表 | `ReplyMessageActivity` |
| `reply/insertReplyMessage` | 新增回覆訊息 | `ReplyMessageActivity` |

### `activity/` — 活動（9 個）

| 端點 | 用途 | 呼叫位置 |
| --- | --- | --- |
| `activity/get` | 活動列表（`type` 1=進行中、2=歷史） | `Acyivity_Activity`、`Activity_record_Activity` |
| `activity/create` | 建立活動 | `insert_activity_Activity` |
| `activity/load` | 載入活動詳細 | `ActivityAdapter`、`RecordAdapter` |
| `activity/edit` | 編輯活動 | `ActivityAdapter` |
| `activity/delete` | 刪除活動 | `ActivityAdapter`、`RecordAdapter` |
| `activity/join` | 報名 | `ActivityAdapter` |
| `activity/cancel` | 取消報名 | `ActivityAdapter` |
| `activity/getTotal` | 取得報名人數 | `ActivityAdapter` |
| `activity/select` | 取得報名名單 | `SelectPeopleActivity` |

### `attraction/` 與 `staple/` — 景點與特產（10 個）

| 端點 | 用途 |
| --- | --- |
| `attraction/` | 景點列表 |
| `attraction/create` | 新增景點 |
| `attraction/load` | 景點詳細 |
| `attraction/updateAttractionData` | 更新景點單一欄位 |
| `attraction/delete` | 刪除景點 |
| `staple/` | 特產列表 |
| `staple/create` | 新增特產 |
| `staple/load` | 特產詳細 |
| `staple/updateStapleData` | 更新特產單一欄位 |
| `staple/delete` | 刪除特產 |

---

## 資料傳輸格式

### 請求

所有請求皆以 Volley 的 `JsonObjectRequest`（`Request.Method.POST`）送出，body 為 JSON。典型寫法：

```java
Map<String, String> map = new HashMap<>();
map.put("account", account);
map.put("password", password);
JSONObject data = new JSONObject(map);

urlSetting = new UrlSetting(LoginActivity.this);
String URL_LOGIN = urlSetting.getUrl() + "user/login";

JsonObjectRequest request = new JsonObjectRequest(
        Request.Method.POST, URL_LOGIN, data,
        response -> { /* 解析 success / data */ },
        error    -> { /* 顯示錯誤對話框 */ });

Volley.newRequestQueue(this).add(request);
```

含圖片的請求則直接建構 `JSONObject`，並將多張圖片放入 `JSONArray`：

```java
JSONObject datas = new JSONObject();
datas.put("postTitle",   name);
datas.put("postContext", context);
datas.put("m_id",        m_id);
datas.put("c_id",        c_id);
datas.put("reply_check", reply_checks);
datas.put("image",       image_array);   // JSONArray of Base64 strings
```

### 回應

```json
{
  "success": "1",
  "data": [ { "...": "..." } ]
}
```

`success` 為字串（非數字），程式一律以 `equals("1")` 之類的字串比較判斷。

### 常見欄位命名

| 前綴 | 意義 | 範例 |
| --- | --- | --- |
| `m_` | member（會員） | `m_id`、`m_name`、`m_acc`、`m_image`、`m_sex` |
| `c_` | community（社區） | `c_id`、`c_name` |
| `p_` | post（公告） | `p_id`、`p_title`、`p_context` |
| `r_` | reply（留言） | `r_id`、`reply`、`replyContext`、`reply_count` |
| `ac_` | activity（活動） | `ac_id`、`ac_name`、`ac_context`、`total_people`、`apply_people` |
| `a_` | attraction（景點）**或** authority（權限） | `a_id`、`a_name`、`a_context` |
| `s_` | staple（特產） | `s_id`、`s_name`、`s_context`、`s_info` |

> ⚠️ `a_id` / `a_name` 在**景點模組**中代表景點 ID／名稱，在 **session 與權限模組**中代表權限 ID／名稱。閱讀程式時需依所在套件判斷語意。

---

## 本機狀態儲存（Session）

專案使用兩組獨立的 SharedPreferences，互不干擾：

| 類別 | SharedPreferences 檔名 | 適用角色 | 儲存欄位 |
| --- | --- | --- | --- |
| `SessionManager` | `LOGIN` | 一般會員／社區幹部 | `IS_LOGIN`、`NAME`、`ACCOUNT`、`USERID`、`C_ID`、`C_NAME`、`IMAGE`、`A_ID`、`AUTHORITY` |
| `ManagerSessionLogin` | `MANAGER_LOGIN` | 系統管理員 | `IS_LOGIN`、`NAME`、`ACCOUNT`、`USERID`、`IMAGE` |

共同提供的方法：

| 方法 | 說明 |
| --- | --- |
| `createSession(...)` | 登入成功後寫入全部欄位並標記 `IS_LOGIN = true` |
| `isLoggin()` | 是否已登入（用於啟動時自動跳轉） |
| `checkLogin()` | 未登入則導回 `LoginActivity` 並結束當前 Activity |
| `getUserDetail()` | 回傳 `HashMap<String, String>` 供各畫面取用 |
| `update(key, value)` | 單一欄位更新（`SessionManager` 專有，供修改姓名／頭像後同步） |
| `logout()` | 清空並導回 `LoginActivity` |

各 Activity 的固定開頭寫法：

```java
sessionManager = new SessionManager(this);
sessionManager.checkLogin();
HashMap<String, String> sessionUserData = sessionManager.getUserDetail();
String sessionUserID   = sessionUserData.get(SessionManager.USERID);
String sessionCommunityId = sessionUserData.get(SessionManager.C_ID);
```

---

## 圖片處理流程

### 上傳

1. 以 `Intent.ACTION_GET_CONTENT`（`EXTRA_ALLOW_MULTIPLE`）開啟系統選圖。
2. 於 `onActivityResult` 中透過 `MediaStore.Images.Media.getBitmap()` 取得 Bitmap。
3. 選取的圖片先放入 RecyclerView（`ImageAdapter` / `ActitityImageAdapter`）供預覽與刪除。
4. 送出前以 `imageToString()` 轉為 Base64 字串：

   ```java
   private String imageToString(Bitmap bitmap){
       ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
       bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
       byte[] imageBytes = outputStream.toByteArray();
       return Base64.encodeToString(imageBytes, Base64.DEFAULT);
   }
   ```

5. 單張圖片放入 JSON 字串欄位，多張圖片放入 `JSONArray`。

> 此 `imageToString()` 在 8 個類別中各自複製了一份（`insert_activity_Activity`、`insert_attraction_Activity`、`insert_post_Activity`、`insert_staple_Activity`、`UpdatePostActivity`、`RegisterActivity`、`CreateCommunityActivity`、`PasswordUpdate_Activity`），為後續重構的明顯目標。

### 下載與顯示

- 一律以 **Glide** 從後端回傳的圖片 URL 載入。
- 頭像使用 `CircleImageView`；無圖時 fallback 為 `R.drawable.user_preset`。
- 詳細頁多圖以 ImageSlider 輪播，並可用 PhotoView 縮放檢視。

---

## 程式碼慣例與命名說明

閱讀本專案前需先了解的幾項既有慣例（皆為原始碼現況，非建議做法）：

1. **`Acyivity` 為 `Activity` 的誤拼**：套件名 `Acyivity`、類別 `Acyivity_Activity` 皆沿用此拼字，且已寫入 `AndroidManifest.xml`。重新命名須同步修改 manifest。
2. **`Intorduce` 為 `Introduce` 的誤拼**：`IntorduceAdapter`、`IntorduceCardViewData` 使用誤拼，但 `IntroduceActivity` 拼字正確。
3. **命名風格混用**：部分類別為 PascalCase（`PostActivity`），部分為 snake_case 開頭小寫（`insert_post_Activity`、`staple_page_Activity`、`PasswordUpdate_Activity`）。
4. **`*CardviewData` / `*CardViewData` 為資料模型**：純 POJO，僅有欄位與 getter/setter，對應 RecyclerView 的一列資料。
5. **`*Adapter` 承擔業務邏輯**：多數 Adapter 不只負責繪製清單，也直接發送 API 請求並彈出對話框（例如 `ActivityAdapter` 有 868 行）。
6. **對話框以自訂 layout + `Dialog` 實作**：`caveat_dialog`（警告／確認）、`success_dialog`（成功）等 layout 在各 Activity 中以 `error_dialog(String)`、`success_dialog(String)` 私有方法重複實作。
7. **每次請求都新建 RequestQueue**：程式各處使用 `Volley.newRequestQueue(this).add(request)`，未共用單一 queue。

---

## 已知問題與注意事項

以下為現況記錄，供後續維護參考：

### 設定與建置

- **API 位址指向區網測試機**：`UrlSetting.getUrl()` 目前回傳 `http://192.168.0.43/usr/public/`，在一般網路環境下必定連線失敗。連線正式站前務必修改。
- **`jcenter()` 已停止服務**：`build.gradle` 中仍列有 `jcenter()`，且 AGP 4.1.2 / Gradle 6.5 版本較舊，在較新的 Android Studio 中可能需要調整建置設定才能同步成功。
- **`local.properties` 不進版控**：內含本機 SDK 絕對路徑，已由 `.gitignore` 排除。

### 安全性

- **明文 HTTP**：`AndroidManifest.xml` 設定 `android:usesCleartextTraffic="true"`，允許未加密連線。
- **密碼以明文傳輸**：登入與註冊直接將 `password` 放入 JSON body 送出。
- **無 Token 機制**：登入後所有請求皆由前端自行帶上 `m_id`、`c_id` 等識別碼，後端無 session／JWT 驗證，具備偽造請求的風險。
- **權限僅在前端隱藏 UI**：`a_id == "3"` 只是隱藏按鈕，並未阻擋 API 呼叫。

### 架構與程式碼

- **`ManagementActivity` 為未實作的空殼**：僅 `setContentView`，無任何邏輯。
- **`CallingDialog` 形同未使用**：唯一的引用位於 `MainActivity` 且已被註解，各 Activity 仍各自實作 `error_dialog()` / `success_dialog()`。
- **社區介紹入口被隱藏**：`MainActivity` 中 `cardview_introduce.setVisibility(View.GONE)`，功能已實作但未開放。
- **兩套幾乎相同的 Session 類別**：`SessionManager` 與 `ManagerSessionLogin` 高度重複。
- **`SessionManager.checkLogin()` / `logout()` 強制轉型為 `MainActivity`**：`((MainActivity) context).finish()` 使其無法安全地在其他 Activity 中呼叫。
- **`imageToString()` 重複 8 次**、對話框方法重複於多數 Activity。
- **`Attraction` 與 `Staple` 兩模組結構高度重複**，可抽為共用基底。
- **無單元測試**：僅存在 Android Studio 產生的兩個樣板測試檔。
- **多項相依未實際使用**：`androidx.wear`、`androidx.navigation`、`com.karumi:dexter`、`com.github.smarteist:autoimageslider` 在原始碼與 layout 中皆無任何引用，可考慮移除以縮減體積。

---

## 授權

本專案未指定授權條款。
