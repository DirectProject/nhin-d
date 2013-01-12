drop table domain;

CREATE TABLE IF NOT EXISTS domain (
    domainName  varchar(255) NOT NULL,
    id    serial PRIMARY KEY,
    createTime  TIMESTAMP DEFAULT NOW() NOT NULL,
    updateTime  TIMESTAMP NOT NULL,
    postmasterAddressId   bigint  NULL,
    status       smallint  DEFAULT 0 NOT NULL 
    );

-- drop index domain_Name_idx on domain;

CREATE UNIQUE INDEX domain_Name_idx ON domain (domainName);    

drop table certificate;

CREATE TABLE IF NOT EXISTS certificate (
    id          serial PRIMARY KEY,
    owner       varchar(255) NOT NULL,
    thumbprint  varchar(64) NOT NULL,     
    createTime  TIMESTAMP DEFAULT NOW() NOT NULL,
    certificateData mediumblob NOT NULL,   
    validStartDate  timestamp NOT NULL,   
    validEndDate timestamp NOT NULL,
    status       smallint  DEFAULT 0 NOT NULL
    );
    
CREATE UNIQUE INDEX certificate_owner_tprint_idx ON certificate (owner(255), thumbprint);      

drop table anchor;

CREATE TABLE anchor (
    id          serial PRIMARY KEY,
   owner       varchar(255),
    thumbprint  varchar(64),
    certificateId bigint NOT NULL,    
    createTime  TIMESTAMP DEFAULT NOW() NOT NULL,
    certificateData mediumblob NOT NULL,   
    validStartDate  timestamp NOT NULL,   
   validEndDate timestamp NOT NULL,
   forIncoming  smallint DEFAULT 1 NOT NULL,
   forOutgoing  smallint DEFAULT 1 NOT NULL,
   status       smallint DEFAULT 0  NOT NULL
    );
    
CREATE UNIQUE INDEX anchor_owner_tprint_idx ON anchor (owner(255), thumbprint);   

drop table address;

CREATE TABLE address (
    id          serial PRIMARY KEY,
   eMailAddress varchar(255) NOT NULL,
   domainId    bigint NOT NULL references domain(id),
   displayName varchar(100) NOT NULL,
    endpoint    varchar(255) NOT NULL,
   createTime  TIMESTAMP DEFAULT NOW() NOT NULL,
   updateTime  TIMESTAMP NOT NULL,
   type        varchar(64) NULL,
   status      smallint DEFAULT 0 NOT NULL
   );
   
CREATE UNIQUE INDEX address_email_idx ON address (eMailAddress(255));     


   
