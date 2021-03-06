CREATE TABLE "CALVIN"."USER_BASE" 
   (	"ID" NUMBER(*,0) NOT NULL ENABLE, 
	"NAME" VARCHAR2(45 BYTE) NOT NULL ENABLE, 
	"GENDER" VARCHAR2(3 BYTE) NOT NULL ENABLE, 
	"AGE" NUMBER(*,0) NOT NULL ENABLE, 
	"ADDRESS" VARCHAR2(100 BYTE) NOT NULL ENABLE, 
	"LVL" NUMBER(*,0) NOT NULL ENABLE
   ) SEGMENT CREATION DEFERRED 
  PCTFREE 10 PCTUSED 40 INITRANS 1 MAXTRANS 255 
 NOCOMPRESS LOGGING
  TABLESPACE "USERS" ;

   COMMENT ON COLUMN "CALVIN"."USER_BASE"."ID" IS '编号';
   COMMENT ON COLUMN "CALVIN"."USER_BASE"."NAME" IS '名字';
   COMMENT ON COLUMN "CALVIN"."USER_BASE"."GENDER" IS '性别';
   COMMENT ON COLUMN "CALVIN"."USER_BASE"."AGE" IS '年龄';
   COMMENT ON COLUMN "CALVIN"."USER_BASE"."ADDRESS" IS '地址';
   COMMENT ON COLUMN "CALVIN"."USER_BASE"."LVL" IS '级别';

  CREATE INDEX "CALVIN"."USER_BASE_IDX1" ON "CALVIN"."USER_BASE" ("ID") 
  PCTFREE 10 INITRANS 2 MAXTRANS 255 COMPUTE STATISTICS 
  TABLESPACE "USERS" ;
  

  
  
CREATE TABLE "CALVIN"."USER_ORDER" 
   (	"ID" NUMBER(*,0) NOT NULL ENABLE, 
	"USER_ID" NUMBER(*,0) NOT NULL ENABLE, 
	"ITEM_NAME" VARCHAR2(45 BYTE) NOT NULL ENABLE, 
	"AMOUNT" NUMBER(*,0) NOT NULL ENABLE, 
	"TOTAL_PRICE" NUMBER(12,2) NOT NULL ENABLE
   ) SEGMENT CREATION DEFERRED 
  PCTFREE 10 PCTUSED 40 INITRANS 1 MAXTRANS 255 
 NOCOMPRESS LOGGING
  TABLESPACE "USERS" ;

   COMMENT ON COLUMN "CALVIN"."USER_ORDER"."ID" IS '编号';
   COMMENT ON COLUMN "CALVIN"."USER_ORDER"."USER_ID" IS '用户编号';
   COMMENT ON COLUMN "CALVIN"."USER_ORDER"."ITEM_NAME" IS '商品名称';
   COMMENT ON COLUMN "CALVIN"."USER_ORDER"."AMOUNT" IS '数量';

  CREATE INDEX "CALVIN"."USER_ORDER_IDX1" ON "CALVIN"."USER_ORDER" ("ID") 
  PCTFREE 10 INITRANS 2 MAXTRANS 255 COMPUTE STATISTICS 
  TABLESPACE "USERS" ;