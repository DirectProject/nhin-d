CREATE TABLE domain (
    domainName  varchar(255) NOT NULL,
    id    bigserial PRIMARY KEY,
    createTime  timestamp with time zone DEFAULT now() NOT NULL,
    updateTime  timestamp with time zone DEFAULT now() NOT NULL,
    postmasterAddressId   bigint  NULL,
    status       smallint  DEFAULT 0 NOT NULL 
    );

CREATE UNIQUE INDEX domain_Name_idx ON domain (domainName);    

CREATE TABLE certificate (
    id          bigserial PRIMARY KEY,
    owner       varchar(400) NOT NULL,
    thumbprint  varchar(64) NOT NULL, 		
    createTime  timestamp with time zone DEFAULT now() NOT NULL,
    certificateData bytea NOT NULL,   
    validStartDate  timestamp with time zone NOT NULL,	
    validEndDate timestamp with time zone NOT NULL,
    status       smallint  DEFAULT 0 NOT NULL
    );
    
CREATE UNIQUE INDEX certificate_owner_tprint_idx ON certificate (owner, thumbprint);      
    
CREATE TABLE anchor (
    id          bigserial PRIMARY KEY,
	owner       varchar(400),
    thumbprint  varchar(64),
    certificateId bigint NOT NULL,    
    createTime  timestamp with time zone DEFAULT now() NOT NULL,
    certificateData bytea NOT NULL,   
    validStartDate  timestamp with time zone NOT NULL,	
	validEndDate timestamp with time zone NOT NULL,
	forIncoming  smallint DEFAULT 1 NOT NULL,
	forOutgoing  smallint DEFAULT 1 NOT NULL,
	status       smallint DEFAULT 0  NOT NULL
    );
    
CREATE UNIQUE INDEX anchor_owner_tprint_idx ON anchor (owner, thumbprint);   

CREATE TABLE address (
    id          bigserial PRIMARY KEY,
	eMailAddress varchar(400) NOT NULL,
	domainId    bigint NOT NULL references domain(id),
	displayName varchar(100) NOT NULL,
    endpoint    varchar(255) NOT NULL,
	createTime  timestamp with time zone DEFAULT now() NOT NULL,
	updateTime  timestamp with time zone DEFAULT now() NOT NULL,
	type        varchar(64) NULL,
	status      smallint DEFAULT 0 NOT NULL
	);
CREATE UNIQUE INDEX address_email_idx ON address (eMailAddress);   	


	
