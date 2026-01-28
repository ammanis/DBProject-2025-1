-- 이름: demo_newmadang.sql
-- 설명: newmadangdb 초기화 및 데이터 생성

/* root 계정으로 접속, newmadangdb 데이터베이스 생성, user 계정 생성 */
/* MySQL Workbench에서 초기화면에서 +를 눌러 root Connection을 만들어 접속한다. */
/* user : newmadang, database : newmadangdb */
/* 사용자 삭제: drop user newmadang@localhost; */


DROP DATABASE IF EXISTS newmadangdb;
CREATE DATABASE IF NOT EXISTS newmadangdb;
USE newmadangdb;

-- user1 계정을 암호 user1 으로 만들고 모든 권한을 부여한다.
-- Create user named 'user1' with password 'user1'
DROP USER IF EXISTS 'user1'@'localhost';
CREATE USER 'user1'@'localhost' IDENTIFIED BY 'user1';
GRANT ALL PRIVILEGES ON newmadangdb.* TO 'user1'@'localhost';
FLUSH PRIVILEGES;

/* === 기존 테이블 삭제 (무시해도 되는 오류) === */
DROP TABLE IF EXISTS PartUsage;
DROP TABLE IF EXISTS Part;
DROP TABLE IF EXISTS Supplier;
DROP TABLE IF EXISTS Maintenance;
DROP TABLE IF EXISTS Employee;
DROP TABLE IF EXISTS Garage;
DROP TABLE IF EXISTS Rental;
DROP TABLE IF EXISTS CamperVan;
DROP TABLE IF EXISTS Company;
DROP TABLE IF EXISTS User;

CREATE TABLE Users (
	user_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(100) NOT NULL,
    name VARCHAR(100) NOT NULL,
    phone VARCHAR(20) UNIQUE NULL,
    email VARCHAR(100) UNIQUE NULL,
    password VARCHAR(255) NOT NULL
);

CREATE TABLE Customer (
  user_id INT PRIMARY KEY,
  license_number VARCHAR(50),
  address VARCHAR(255),
  previous_rental_date DATE,
  preferred_camper_type VARCHAR(50),
  FOREIGN KEY (user_id) REFERENCES Users(user_id)
);

CREATE TABLE Company (
	company_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    address VARCHAR(255),
    phone VARCHAR(20),
    contact_person VARCHAR(50),
    email VARCHAR(100)
);

CREATE TABLE CamperVan (
	camper_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    license_plate VARCHAR(20) UNIQUE NOT NULL,
    capacity INT,
    fuel_type VARCHAR(20),
    status ENUM('대여 가능', '대여 중', '정비 중'),
    daily_cost DECIMAL(10,2) NOT NULL,
    registration_date DATE,
    company_id INT,
    FOREIGN KEY (company_id) REFERENCES Company(company_id)
);

CREATE TABLE Rental (
	rental_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    camper_id INT NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    rental_cost DECIMAL(10,2),
    additional_charge DECIMAL(10,2),
    total_cost DECIMAL(10,2),
    payment_due_date DATE,
    FOREIGN KEY (user_id) REFERENCES Users(user_id),
    FOREIGN KEY (camper_id) REFERENCES CamperVan(camper_id)
);

CREATE TABLE Employee (
	employee_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    phone VARCHAR(20),
    address VARCHAR(255),
    salary DECIMAL(10,2),
    department VARCHAR(50),
    dependents INT DEFAULT 0,
    role ENUM('관리', '사무', '정비')
);

CREATE TABLE Garage (
	garage_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    address VARCHAR(255),
    phone VARCHAR(20),
    contact_person VARCHAR(50),
    email VARCHAR(100)
);

CREATE TABLE Maintenance (
	maintenance_id INT AUTO_INCREMENT PRIMARY KEY,
    camper_id INT NOT NULL,
    maintenance_type ENUM('내부','외부') NOT NULL,
    maintenance_date DATE NOT NULL,
    maintenance_time INT,
    cost DECIMAL(10,2),
    employee_id INT,
    garage_id INT,
    user_id INT,
    FOREIGN KEY (camper_id) REFERENCES CamperVan(camper_id),
    FOREIGN KEY (employee_id) REFERENCES Employee(employee_id),
    FOREIGN KEY (garage_id) REFERENCES Garage(garage_id),
    FOREIGN KEY (user_id) REFERENCES Users(user_id)
);

CREATE TABLE Supplier (
	supplier_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    address VARCHAR(255),
    phone VARCHAR(20)
);

CREATE TABLE Part (
	part_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    unit_price DECIMAL(10,2),
    stock_quantity INT,
    supply_date DATE,
    supplier_id INT,
    FOREIGN KEY (supplier_id) REFERENCES Supplier(supplier_id)
);

CREATE TABLE PartUsage(
	part_usage_id INT AUTO_INCREMENT PRIMARY KEY,
    maintenance_id INT NOT NULL,
    part_id INT NOT NULL,
    quantity_used INT NOT NULL,
    FOREIGN KEY (maintenance_id) REFERENCES Maintenance(maintenance_id),
    FOREIGN KEY (part_id) REFERENCES Part(part_id)
);

/* === 초기 데이터 삽입 (예시, 선택사항) === */
INSERT INTO Users (username, name, phone, email, password) VALUES
('kim','김아리', '010-1111-1111', 'kim.ari@naver.com', '1234'),
('lee','이보람', '010-2222-2222', 'lee.boram@korea.com', '1234'),
('park','박찬희', '010-3333-3333', 'park.chanhee@daum.net', '1234'),
('choi','최다은', '010-4444-4444', 'choi.daeun@hanmail.net', '1234'),
('jong','정이준', '010-5555-5555', 'jung.eejun@gmail.com', '1234'),
('seo','서피오나', '010-6666-6666', 'seo.fiona@naver.com', '1234'),
('han','한지훈', '010-7777-7777', 'han.jihun@daum.net', '1234'),
('yun','윤혜린', '010-8888-8888', 'yoon.hyerin@kakao.com', '1234'),
('baek','백이안', '010-9999-9999', 'baek.ian@korea.com', '1234'),
('gwon','권재인', '010-1010-1010', 'kwon.jaein@naver.com', '1234'),
('im','임규현', '010-1212-1212', 'lim.kyuhyun@hanmail.net', '1234'),
('min','민루시', '010-1313-1313', 'min.lucy@daum.net', '1234');

INSERT INTO Customer (user_id, license_number, address, previous_rental_date, preferred_camper_type) VALUES
(1, '12가1234567', '서울특별시 강남구 테헤란로 123', '2024-07-10', '소형'),
(2, '34나7654321', '부산광역시 해운대구 달맞이길 45', '2023-12-01', '중형'),
(3, '56다2345678', '대구광역시 수성구 들안로 98', NULL, '대형'),
(4, '78라8765432', '인천광역시 미추홀구 용현동 101', '2022-10-15', '소형'),
(5, '90마1234987', '경기도 수원시 팔달구 매산로 55', NULL, '중형'),
(6, '12바9876543', '전라북도 전주시 덕진구 백제대로 77', '2024-03-25', '대형'),
(7, '34사3456789', '경상남도 창원시 의창구 중앙대로 80', '2023-06-30', '소형'),
(8, '56아1122334', '충청북도 청주시 상당구 상당로 12', NULL, '중형'),
(9, '78자5566778', '강원도 춘천시 중앙로 23', '2022-12-05', '대형'),
(10, '90차8899001', '경상북도 포항시 북구 삼호로 9', '2023-11-01', '중형'),
(11, '12카3344556', '제주특별자치도 제주시 한라로 101', NULL, '소형'),
(12, '34타9988776', '울산광역시 남구 삼산로 45', '2024-05-10', '대형');

INSERT INTO Company (name, address, phone, contact_person, email) VALUES
('모험주식회사', '서울특별시', '02-123-4567', '송씨', 'contact@adventure.co'),
('자유여행사', '부산광역시', '051-987-6543', '장씨', 'info@freedom.com'),
('캠퍼월드', '인천광역시', '032-555-7890', '강씨', 'support@camperworld.com'),
('노매드휠스', '대구광역시', '053-888-3333', '황씨', 'nomad@wheels.kr'),
('해피캠퍼스', '대전광역시', '042-777-1212', '남씨', 'hello@happycampers.kr'),
('고밴렌탈스', '제주특별자치도', '064-444-1122', '유씨', 'book@govan.jeju'),
('캠퍼렌트', '성남시', '031-888-0001', '배씨', 'ask@camperrent.kr'),
('코리아캠퍼스', '광주광역시', '062-321-4567', '고씨', 'help@koreacampers.kr'),
('선셋트립스', '수원시', '031-222-5678', '주씨', 'sunset@trips.kr'),
('로드트립주식회사', '울산광역시', '052-333-9999', '하씨', 'road@trip.co.kr'),
('캠프메이트', '안양시', '031-555-1212', '조씨', 'mates@camp.kr'),
('트레일완더', '춘천시', '033-111-2222', '신씨', 'trail@wander.kr');

INSERT INTO CamperVan (name, license_plate, capacity, fuel_type, status, daily_cost, registration_date, company_id) VALUES
('탐험자 1호', '11가1234', 4, '디젤', '대여 가능', 80000, '2023-03-01', 1),
('자유 밴', '22나5678', 2, '가솔린', '대여 중', 70000, '2023-04-12', 2),
('선시커', '33다9012', 3, '디젤', '정비 중', 85000, '2022-12-20', 3),
('로드킹', '44라3456', 5, '디젤', '대여 가능', 90000, '2023-01-05', 4),
('캠프 크루저', '55마7890', 2, '전기', '대여 가능', 95000, '2023-02-18', 5),
('반고', '66바1234', 4, '디젤', '대여 가능', 82000, '2023-03-10', 6),
('노매드 라이드', '77사5678', 3, '가솔린', '대여 중', 78000, '2023-04-01', 7),
('비스타 밴', '88아9012', 5, '디젤', '대여 가능', 87000, '2023-05-12', 8),
('그린 로머', '99자3456', 3, '전기', '정비 중', 92000, '2023-06-15', 9),
('오션 왜건', '00차7890', 4, '디젤', '대여 중', 83000, '2023-07-07', 10),
('트레일 버디', '12카1234', 3, '가솔린', '대여 가능', 76000, '2023-08-01', 11),
('코리아 캠퍼', '34타5678', 4, '디젤', '대여 가능', 88000, '2023-09-20', 12);

INSERT INTO Rental (user_id, camper_id, start_date, end_date, rental_cost, additional_charge, total_cost, payment_due_date) VALUES
(1, 1, '2024-01-05', '2024-01-10', 400000, 20000, 420000, '2024-01-15'),
(2, 2, '2024-02-01', '2024-02-05', 350000, 15000, 365000, '2024-02-07'),
(3, 3, '2024-03-10', '2024-03-13', 255000, 10000, 265000, '2024-03-15'),
(4, 4, '2024-04-01', '2024-04-06', 540000, 30000, 570000, '2024-04-10'),
(5, 5, '2024-05-03', '2024-05-07', 380000, 25000, 405000, '2024-05-10'),
(6, 6, '2024-06-10', '2024-06-12', 164000, 8000, 172000, '2024-06-14'),
(7, 7, '2024-07-01', '2024-07-05', 312000, 12000, 324000, '2024-07-08'),
(8, 8, '2024-08-15', '2024-08-20', 435000, 15000, 450000, '2024-08-22'),
(9, 9, '2024-09-01', '2024-09-04', 276000, 9000, 285000, '2024-09-06'),
(10, 10, '2024-10-10', '2024-10-15', 415000, 20000, 435000, '2024-10-18'),
(11, 11, '2024-11-01', '2024-11-05', 304000, 10000, 314000, '2024-11-07'),
(12, 12, '2024-12-20', '2024-12-25', 440000, 25000, 465000, '2024-12-28');

INSERT INTO Garage (name, address, phone, contact_person, email) VALUES
('픽잇 정비소', '서울', '02-123-4567', '김 씨', 'fixit@garage.kr'),
('오토케어', '부산', '051-987-6543', '최 씨', 'care@auto.kr'),
('밴픽스', '인천', '032-555-7890', '이 씨', 'help@vanfix.com'),
('메카프로', '대구', '053-888-3333', '박 씨', 'service@mechapro.kr'),
('개러지원', '대전', '042-777-1212', '정 씨', 'info@garageone.kr'),
('모터클리닉', '제주', '064-444-1122', '황 씨', 'motor@clinic.kr'),
('케이-오토', '광주', '062-321-4567', '한 씨', 'support@kauto.kr'),
('픽프로', '수원', '031-222-5678', '강 씨', 'fix@pro.kr'),
('로드리페어', '울산', '052-333-9999', '구 씨', 'repair@road.kr'),
('개러지메이트', '안양', '031-555-1212', '유 씨', 'contact@garagemate.kr'),
('캠프픽스', '춘천', '033-111-2222', '조 씨', 'fix@camp.kr'),
('탑기어', '성남', '031-888-1111', '신 씨', 'top@gear.kr');

INSERT INTO Employee (name, phone, address, salary, department, dependents, role) VALUES
('김관리', '010-1111-2222', '서울 종로구 종각로 10', 4500000, '경영지원팀', 2, '관리'),
('이사무', '010-3333-4444', '성남시 분당구 정자일로 7', 3800000, '총무팀', 0, '사무'),
('박정비', '010-5555-6666', '인천 연수구 송도로 88', 4000000, '정비팀', 1, '정비'),
('최관리', '010-7777-8888', '부산 해운대구 해운로 9', 4700000, '경영기획팀', 3, '관리'),
('정사무', '010-8888-9999', '수원시 장안구 파장로 31', 3900000, '회계팀', 1, '사무'),
('오정비', '010-9999-0000', '대전 유성구 계룡로 12', 4200000, '정비팀', 2, '정비'),
('한관리', '010-2222-3333', '광주 북구 문흥동 202', 4600000, '전략기획팀', 1, '관리'),
('유사무', '010-4444-5555', '울산 중구 태화강로 55', 3850000, '총무팀', 0, '사무'),
('조정비', '010-6666-7777', '대구 달서구 월배로 81', 4100000, '정비팀', 3, '정비'),
('장관리', '010-1234-5678', '제주 서귀포시 중문로 22', 4550000, '기획실', 0, '관리'),
('신사무', '010-8765-4321', '청주 흥덕구 복대동 300', 3950000, '문서팀', 2, '사무'),
('배정비', '010-1122-3344', '창원 마산합포구 문화동 87', 4300000, '정비팀', 1, '정비');

INSERT INTO Maintenance (camper_id, maintenance_type, maintenance_date, maintenance_time, cost, employee_id, garage_id, user_id) VALUES
(1, '내부', '2025-04-12', 90, 120000, 3, NULL, 1),
(2, '외부', '2025-04-20', 60, 180000, NULL, 1, 2),
(3, '내부', '2025-05-01', 120, 200000, 6, NULL, 3),
(4, '외부', '2025-05-10', 45, 90000, NULL, 2, 4),
(5, '내부', '2025-05-15', 100, 150000, 9, NULL, 5),
(1, '외부', '2025-05-22', 70, 170000, NULL, 3, 6),
(2, '내부', '2025-05-28', 95, 140000, 12, NULL, 7),
(3, '외부', '2025-06-01', 60, 130000, NULL, 1, 8),
(4, '내부', '2025-06-05', 80, 160000, 6, NULL, 9),
(5, '외부', '2025-06-10', 110, 190000, NULL, 2, 10),
(1, '내부', '2025-06-15', 85, 125000, 3, NULL, 11),
(2, '외부', '2025-06-20', 50, 100000, NULL, 3, 12);

INSERT INTO Supplier (name, address, phone) VALUES
('파트스코', '서울', '02-111-2222'),
('오토파트스 주식회사', '부산', '051-333-4444'),
('코리아 서플라이즈', '인천', '032-555-6666'),
('밴기어', '대구', '053-777-8888'),
('픽스마트', '대전', '042-999-0000'),
('개러지서플라이', '제주', '064-123-4567'),
('프로툴즈', '광주', '062-789-0123'),
('메카파트스', '수원', '031-345-6789'),
('로드파트스', '울산', '052-567-8901'),
('탑툴즈', '안양', '031-654-3210'),
('렌치하우스', '춘천', '033-234-5678'),
('툴코어', '성남', '031-876-5432');

INSERT INTO Part (name, unit_price, stock_quantity, supply_date, supplier_id) VALUES
('오일 필터', 25000, 30, '2024-01-01', 1),
('브레이크 패드', 45000, 20, '2024-01-05', 2),
('에어 필터', 15000, 50, '2024-01-10', 3),
('와이퍼 블레이드', 12000, 40, '2024-01-15', 4),
('배터리', 80000, 10, '2024-02-01', 5),
('스파크 플러그', 18000, 60, '2024-02-10', 6),
('헤드라이트', 65000, 15, '2024-02-20', 7),
('테일 라이트', 55000, 18, '2024-03-01', 8),
('라디에이터', 90000, 8, '2024-03-05', 9),
('타이밍 벨트', 70000, 12, '2024-03-10', 10),
('알터네이터', 120000, 6, '2024-03-20', 11),
('연료 펌프', 95000, 7, '2024-03-25', 12);

INSERT INTO PartUsage (maintenance_id, part_id, quantity_used) VALUES
(1, 1, 1),
(2, 2, 2),
(3, 3, 1),
(4, 4, 2),
(5, 5, 1),
(6, 6, 4),
(7, 7, 1),
(8, 8, 2),
(9, 9, 1),
(10, 10, 1),
(11, 11, 1),
(12, 12, 1);
