

/* this ALWAYS GENERATED file contains the definitions for the interfaces */


 /* File created by MIDL compiler version 7.00.0555 */
/* at Tue Aug 10 21:55:51 2010
 */
/* Compiler settings for C:\Program Files\Microsoft SDKs\Windows\v6.0A\Include\msado15.idl:
    Oicf, W1, Zp8, env=Win32 (32b run), target_arch=X86 7.00.0555 
    protocol : dce , ms_ext, c_ext, robust
    error checks: allocation ref bounds_check enum stub_data 
    VC __declspec() decoration level: 
         __declspec(uuid()), __declspec(selectany), __declspec(novtable)
         DECLSPEC_UUID(), MIDL_INTERFACE()
*/
/* @@MIDL_FILE_HEADING(  ) */

#pragma warning( disable: 4049 )  /* more than 64k source lines */


/* verify that the <rpcndr.h> version is high enough to compile this file*/
#ifndef __REQUIRED_RPCNDR_H_VERSION__
#define __REQUIRED_RPCNDR_H_VERSION__ 475
#endif

#include "rpc.h"
#include "rpcndr.h"

#ifndef __RPCNDR_H_VERSION__
#error this stub requires an updated version of <rpcndr.h>
#endif // __RPCNDR_H_VERSION__

#ifndef COM_NO_WINDOWS_H
#include "windows.h"
#include "ole2.h"
#endif /*COM_NO_WINDOWS_H*/

#ifndef __msado15_h__
#define __msado15_h__

#if defined(_MSC_VER) && (_MSC_VER >= 1020)
#pragma once
#endif

/* Forward Declarations */ 

#ifndef ___Collection_FWD_DEFINED__
#define ___Collection_FWD_DEFINED__
typedef interface _Collection _Collection;
#endif 	/* ___Collection_FWD_DEFINED__ */


#ifndef ___DynaCollection_FWD_DEFINED__
#define ___DynaCollection_FWD_DEFINED__
typedef interface _DynaCollection _DynaCollection;
#endif 	/* ___DynaCollection_FWD_DEFINED__ */


#ifndef ___ADO_FWD_DEFINED__
#define ___ADO_FWD_DEFINED__
typedef interface _ADO _ADO;
#endif 	/* ___ADO_FWD_DEFINED__ */


#ifndef __Properties_FWD_DEFINED__
#define __Properties_FWD_DEFINED__
typedef interface Properties Properties;
#endif 	/* __Properties_FWD_DEFINED__ */


#ifndef __Property_FWD_DEFINED__
#define __Property_FWD_DEFINED__
typedef interface Property Property;
#endif 	/* __Property_FWD_DEFINED__ */


#ifndef __Error_FWD_DEFINED__
#define __Error_FWD_DEFINED__
typedef interface Error Error;
#endif 	/* __Error_FWD_DEFINED__ */


#ifndef __Errors_FWD_DEFINED__
#define __Errors_FWD_DEFINED__
typedef interface Errors Errors;
#endif 	/* __Errors_FWD_DEFINED__ */


#ifndef __Command15_FWD_DEFINED__
#define __Command15_FWD_DEFINED__
typedef interface Command15 Command15;
#endif 	/* __Command15_FWD_DEFINED__ */


#ifndef __Command25_FWD_DEFINED__
#define __Command25_FWD_DEFINED__
typedef interface Command25 Command25;
#endif 	/* __Command25_FWD_DEFINED__ */


#ifndef ___Command_FWD_DEFINED__
#define ___Command_FWD_DEFINED__
typedef interface _Command _Command;
#endif 	/* ___Command_FWD_DEFINED__ */


#ifndef __Connection15_FWD_DEFINED__
#define __Connection15_FWD_DEFINED__
typedef interface Connection15 Connection15;
#endif 	/* __Connection15_FWD_DEFINED__ */


#ifndef ___Connection_FWD_DEFINED__
#define ___Connection_FWD_DEFINED__
typedef interface _Connection _Connection;
#endif 	/* ___Connection_FWD_DEFINED__ */


#ifndef __Recordset15_FWD_DEFINED__
#define __Recordset15_FWD_DEFINED__
typedef interface Recordset15 Recordset15;
#endif 	/* __Recordset15_FWD_DEFINED__ */


#ifndef __Recordset20_FWD_DEFINED__
#define __Recordset20_FWD_DEFINED__
typedef interface Recordset20 Recordset20;
#endif 	/* __Recordset20_FWD_DEFINED__ */


#ifndef __Recordset21_FWD_DEFINED__
#define __Recordset21_FWD_DEFINED__
typedef interface Recordset21 Recordset21;
#endif 	/* __Recordset21_FWD_DEFINED__ */


#ifndef ___Recordset_FWD_DEFINED__
#define ___Recordset_FWD_DEFINED__
typedef interface _Recordset _Recordset;
#endif 	/* ___Recordset_FWD_DEFINED__ */


#ifndef __Fields15_FWD_DEFINED__
#define __Fields15_FWD_DEFINED__
typedef interface Fields15 Fields15;
#endif 	/* __Fields15_FWD_DEFINED__ */


#ifndef __Fields20_FWD_DEFINED__
#define __Fields20_FWD_DEFINED__
typedef interface Fields20 Fields20;
#endif 	/* __Fields20_FWD_DEFINED__ */


#ifndef __Fields_FWD_DEFINED__
#define __Fields_FWD_DEFINED__
typedef interface Fields Fields;
#endif 	/* __Fields_FWD_DEFINED__ */


#ifndef __Field15_FWD_DEFINED__
#define __Field15_FWD_DEFINED__
typedef interface Field15 Field15;
#endif 	/* __Field15_FWD_DEFINED__ */


#ifndef __Field20_FWD_DEFINED__
#define __Field20_FWD_DEFINED__
typedef interface Field20 Field20;
#endif 	/* __Field20_FWD_DEFINED__ */


#ifndef __Field_FWD_DEFINED__
#define __Field_FWD_DEFINED__
typedef interface Field Field;
#endif 	/* __Field_FWD_DEFINED__ */


#ifndef ___Parameter_FWD_DEFINED__
#define ___Parameter_FWD_DEFINED__
typedef interface _Parameter _Parameter;
#endif 	/* ___Parameter_FWD_DEFINED__ */


#ifndef __Parameters_FWD_DEFINED__
#define __Parameters_FWD_DEFINED__
typedef interface Parameters Parameters;
#endif 	/* __Parameters_FWD_DEFINED__ */


#ifndef ___Record_FWD_DEFINED__
#define ___Record_FWD_DEFINED__
typedef interface _Record _Record;
#endif 	/* ___Record_FWD_DEFINED__ */


#ifndef ___Stream_FWD_DEFINED__
#define ___Stream_FWD_DEFINED__
typedef interface _Stream _Stream;
#endif 	/* ___Stream_FWD_DEFINED__ */


#ifndef __ADODebugging_FWD_DEFINED__
#define __ADODebugging_FWD_DEFINED__
typedef interface ADODebugging ADODebugging;
#endif 	/* __ADODebugging_FWD_DEFINED__ */


#ifndef __ConnectionEventsVt_FWD_DEFINED__
#define __ConnectionEventsVt_FWD_DEFINED__
typedef interface ConnectionEventsVt ConnectionEventsVt;
#endif 	/* __ConnectionEventsVt_FWD_DEFINED__ */


#ifndef __RecordsetEventsVt_FWD_DEFINED__
#define __RecordsetEventsVt_FWD_DEFINED__
typedef interface RecordsetEventsVt RecordsetEventsVt;
#endif 	/* __RecordsetEventsVt_FWD_DEFINED__ */


#ifndef __ConnectionEvents_FWD_DEFINED__
#define __ConnectionEvents_FWD_DEFINED__
typedef interface ConnectionEvents ConnectionEvents;
#endif 	/* __ConnectionEvents_FWD_DEFINED__ */


#ifndef __RecordsetEvents_FWD_DEFINED__
#define __RecordsetEvents_FWD_DEFINED__
typedef interface RecordsetEvents RecordsetEvents;
#endif 	/* __RecordsetEvents_FWD_DEFINED__ */


#ifndef __ADOConnectionConstruction15_FWD_DEFINED__
#define __ADOConnectionConstruction15_FWD_DEFINED__
typedef interface ADOConnectionConstruction15 ADOConnectionConstruction15;
#endif 	/* __ADOConnectionConstruction15_FWD_DEFINED__ */


#ifndef __ADOConnectionConstruction_FWD_DEFINED__
#define __ADOConnectionConstruction_FWD_DEFINED__
typedef interface ADOConnectionConstruction ADOConnectionConstruction;
#endif 	/* __ADOConnectionConstruction_FWD_DEFINED__ */


#ifndef __ADORecordsetConstruction_FWD_DEFINED__
#define __ADORecordsetConstruction_FWD_DEFINED__
typedef interface ADORecordsetConstruction ADORecordsetConstruction;
#endif 	/* __ADORecordsetConstruction_FWD_DEFINED__ */


#ifndef __ADOCommandConstruction_FWD_DEFINED__
#define __ADOCommandConstruction_FWD_DEFINED__
typedef interface ADOCommandConstruction ADOCommandConstruction;
#endif 	/* __ADOCommandConstruction_FWD_DEFINED__ */


#ifndef __ADORecordConstruction_FWD_DEFINED__
#define __ADORecordConstruction_FWD_DEFINED__
typedef interface ADORecordConstruction ADORecordConstruction;
#endif 	/* __ADORecordConstruction_FWD_DEFINED__ */


#ifndef __ADOStreamConstruction_FWD_DEFINED__
#define __ADOStreamConstruction_FWD_DEFINED__
typedef interface ADOStreamConstruction ADOStreamConstruction;
#endif 	/* __ADOStreamConstruction_FWD_DEFINED__ */


#ifndef __Connection_FWD_DEFINED__
#define __Connection_FWD_DEFINED__

#ifdef __cplusplus
typedef class Connection Connection;
#else
typedef struct Connection Connection;
#endif /* __cplusplus */

#endif 	/* __Connection_FWD_DEFINED__ */


#ifndef __Command_FWD_DEFINED__
#define __Command_FWD_DEFINED__

#ifdef __cplusplus
typedef class Command Command;
#else
typedef struct Command Command;
#endif /* __cplusplus */

#endif 	/* __Command_FWD_DEFINED__ */


#ifndef __Recordset_FWD_DEFINED__
#define __Recordset_FWD_DEFINED__

#ifdef __cplusplus
typedef class Recordset Recordset;
#else
typedef struct Recordset Recordset;
#endif /* __cplusplus */

#endif 	/* __Recordset_FWD_DEFINED__ */


#ifndef __Parameter_FWD_DEFINED__
#define __Parameter_FWD_DEFINED__

#ifdef __cplusplus
typedef class Parameter Parameter;
#else
typedef struct Parameter Parameter;
#endif /* __cplusplus */

#endif 	/* __Parameter_FWD_DEFINED__ */


#ifndef __Record_FWD_DEFINED__
#define __Record_FWD_DEFINED__

#ifdef __cplusplus
typedef class Record Record;
#else
typedef struct Record Record;
#endif /* __cplusplus */

#endif 	/* __Record_FWD_DEFINED__ */


#ifndef __Stream_FWD_DEFINED__
#define __Stream_FWD_DEFINED__

#ifdef __cplusplus
typedef class Stream Stream;
#else
typedef struct Stream Stream;
#endif /* __cplusplus */

#endif 	/* __Stream_FWD_DEFINED__ */


/* header files for imported files */
#include "oaidl.h"

#ifdef __cplusplus
extern "C"{
#endif 


/* interface __MIDL_itf_msado15_0000_0000 */
/* [local] */ 




































typedef /* [uuid][public] */  DECLSPEC_UUID("54D8B4B9-663B-4a9c-95F6-0E749ABD70F1") long ADO_LONGPTR;

typedef /* [public][public][public][public][public][public][uuid] */  DECLSPEC_UUID("0000051B-0000-0010-8000-00AA006D2EA4") 
enum __MIDL___MIDL_itf_msado15_0000_0000_0001
    {	adOpenUnspecified	= -1,
	adOpenForwardOnly	= 0,
	adOpenKeyset	= 1,
	adOpenDynamic	= 2,
	adOpenStatic	= 3
    } 	CursorTypeEnum;

typedef /* [public][public][uuid] */  DECLSPEC_UUID("0000051C-0000-0010-8000-00AA006D2EA4") 
enum __MIDL___MIDL_itf_msado15_0000_0000_0002
    {	adHoldRecords	= 256,
	adMovePrevious	= 512,
	adAddNew	= 16778240,
	adDelete	= 16779264,
	adUpdate	= 16809984,
	adBookmark	= 8192,
	adApproxPosition	= 16384,
	adUpdateBatch	= 65536,
	adResync	= 131072,
	adNotify	= 262144,
	adFind	= 524288,
	adSeek	= 4194304,
	adIndex	= 8388608
    } 	CursorOptionEnum;

typedef /* [public][public][public][public][public][public][public][uuid] */  DECLSPEC_UUID("0000051D-0000-0010-8000-00AA006D2EA4") 
enum __MIDL___MIDL_itf_msado15_0000_0000_0003
    {	adLockUnspecified	= -1,
	adLockReadOnly	= 1,
	adLockPessimistic	= 2,
	adLockOptimistic	= 3,
	adLockBatchOptimistic	= 4
    } 	LockTypeEnum;

typedef /* [public][uuid] */  DECLSPEC_UUID("0000051E-0000-0010-8000-00AA006D2EA4") 
enum __MIDL___MIDL_itf_msado15_0000_0000_0004
    {	adOptionUnspecified	= -1,
	adAsyncExecute	= 16,
	adAsyncFetch	= 32,
	adAsyncFetchNonBlocking	= 64,
	adExecuteNoRecords	= 128,
	adExecuteStream	= 1024,
	adExecuteRecord	= 2048
    } 	ExecuteOptionEnum;

typedef /* [public][uuid] */  DECLSPEC_UUID("00000541-0000-0010-8000-00AA006D2EA4") 
enum __MIDL___MIDL_itf_msado15_0000_0000_0005
    {	adConnectUnspecified	= -1,
	adAsyncConnect	= 16
    } 	ConnectOptionEnum;

typedef /* [public][public][public][uuid] */  DECLSPEC_UUID("00000532-0000-0010-8000-00AA006D2EA4") 
enum __MIDL___MIDL_itf_msado15_0000_0000_0006
    {	adStateClosed	= 0,
	adStateOpen	= 1,
	adStateConnecting	= 2,
	adStateExecuting	= 4,
	adStateFetching	= 8
    } 	ObjectStateEnum;

typedef /* [public][public][public][public][public][uuid] */  DECLSPEC_UUID("0000052F-0000-0010-8000-00AA006D2EA4") 
enum __MIDL___MIDL_itf_msado15_0000_0000_0007
    {	adUseNone	= 1,
	adUseServer	= 2,
	adUseClient	= 3,
	adUseClientBatch	= 3
    } 	CursorLocationEnum;

typedef /* [public][public][public][public][public][public][public][public][public][public][uuid] */  DECLSPEC_UUID("0000051F-0000-0010-8000-00AA006D2EA4") 
enum __MIDL___MIDL_itf_msado15_0000_0000_0008
    {	adEmpty	= 0,
	adTinyInt	= 16,
	adSmallInt	= 2,
	adInteger	= 3,
	adBigInt	= 20,
	adUnsignedTinyInt	= 17,
	adUnsignedSmallInt	= 18,
	adUnsignedInt	= 19,
	adUnsignedBigInt	= 21,
	adSingle	= 4,
	adDouble	= 5,
	adCurrency	= 6,
	adDecimal	= 14,
	adNumeric	= 131,
	adBoolean	= 11,
	adError	= 10,
	adUserDefined	= 132,
	adVariant	= 12,
	adIDispatch	= 9,
	adIUnknown	= 13,
	adGUID	= 72,
	adDate	= 7,
	adDBDate	= 133,
	adDBTime	= 134,
	adDBTimeStamp	= 135,
	adBSTR	= 8,
	adChar	= 129,
	adVarChar	= 200,
	adLongVarChar	= 201,
	adWChar	= 130,
	adVarWChar	= 202,
	adLongVarWChar	= 203,
	adBinary	= 128,
	adVarBinary	= 204,
	adLongVarBinary	= 205,
	adChapter	= 136,
	adFileTime	= 64,
	adPropVariant	= 138,
	adVarNumeric	= 139,
	adArray	= 0x2000
    } 	DataTypeEnum;

typedef /* [public][public][public][uuid] */  DECLSPEC_UUID("00000525-0000-0010-8000-00AA006D2EA4") 
enum __MIDL___MIDL_itf_msado15_0000_0000_0009
    {	adFldUnspecified	= -1,
	adFldMayDefer	= 0x2,
	adFldUpdatable	= 0x4,
	adFldUnknownUpdatable	= 0x8,
	adFldFixed	= 0x10,
	adFldIsNullable	= 0x20,
	adFldMayBeNull	= 0x40,
	adFldLong	= 0x80,
	adFldRowID	= 0x100,
	adFldRowVersion	= 0x200,
	adFldCacheDeferred	= 0x1000,
	adFldIsChapter	= 0x2000,
	adFldNegativeScale	= 0x4000,
	adFldKeyColumn	= 0x8000,
	adFldIsRowURL	= 0x10000,
	adFldIsDefaultStream	= 0x20000,
	adFldIsCollection	= 0x40000
    } 	FieldAttributeEnum;

typedef /* [public][public][uuid] */  DECLSPEC_UUID("00000526-0000-0010-8000-00AA006D2EA4") 
enum __MIDL___MIDL_itf_msado15_0000_0000_0010
    {	adEditNone	= 0,
	adEditInProgress	= 1,
	adEditAdd	= 2,
	adEditDelete	= 4
    } 	EditModeEnum;

typedef /* [public][uuid] */  DECLSPEC_UUID("00000527-0000-0010-8000-00AA006D2EA4") 
enum __MIDL___MIDL_itf_msado15_0000_0000_0011
    {	adRecOK	= 0,
	adRecNew	= 1,
	adRecModified	= 2,
	adRecDeleted	= 4,
	adRecUnmodified	= 8,
	adRecInvalid	= 16,
	adRecMultipleChanges	= 64,
	adRecPendingChanges	= 128,
	adRecCanceled	= 256,
	adRecCantRelease	= 1024,
	adRecConcurrencyViolation	= 2048,
	adRecIntegrityViolation	= 4096,
	adRecMaxChangesExceeded	= 8192,
	adRecObjectOpen	= 16384,
	adRecOutOfMemory	= 32768,
	adRecPermissionDenied	= 65536,
	adRecSchemaViolation	= 131072,
	adRecDBDeleted	= 262144
    } 	RecordStatusEnum;

typedef /* [public][uuid] */  DECLSPEC_UUID("00000542-0000-0010-8000-00AA006D2EA4") 
enum __MIDL___MIDL_itf_msado15_0000_0000_0012
    {	adGetRowsRest	= -1
    } 	GetRowsOptionEnum;

typedef /* [public][public][public][public][public][public][uuid] */  DECLSPEC_UUID("00000528-0000-0010-8000-00AA006D2EA4") 
enum __MIDL___MIDL_itf_msado15_0000_0000_0013
    {	adPosUnknown	= -1,
	adPosBOF	= -2,
	adPosEOF	= -3
    } 	PositionEnum;

typedef /* [uuid][public] */  DECLSPEC_UUID("A56187C5-D690-4037-AE32-A00EDC376AC3") PositionEnum PositionEnum_Param;

typedef /* [public] */ 
enum __MIDL___MIDL_itf_msado15_0000_0000_0014
    {	adBookmarkCurrent	= 0,
	adBookmarkFirst	= 1,
	adBookmarkLast	= 2
    } 	BookmarkEnum;

typedef /* [public][public][public][uuid] */  DECLSPEC_UUID("00000540-0000-0010-8000-00AA006D2EA4") 
enum __MIDL___MIDL_itf_msado15_0000_0000_0015
    {	adMarshalAll	= 0,
	adMarshalModifiedOnly	= 1
    } 	MarshalOptionsEnum;

typedef /* [public][public][public][public][public][public][uuid] */  DECLSPEC_UUID("00000543-0000-0010-8000-00AA006D2EA4") 
enum __MIDL___MIDL_itf_msado15_0000_0000_0016
    {	adAffectCurrent	= 1,
	adAffectGroup	= 2,
	adAffectAll	= 3,
	adAffectAllChapters	= 4
    } 	AffectEnum;

typedef /* [public][public][public][uuid] */  DECLSPEC_UUID("00000544-0000-0010-8000-00AA006D2EA4") 
enum __MIDL___MIDL_itf_msado15_0000_0000_0017
    {	adResyncUnderlyingValues	= 1,
	adResyncAllValues	= 2
    } 	ResyncEnum;

typedef /* [public][public][uuid] */  DECLSPEC_UUID("00000545-0000-0010-8000-00AA006D2EA4") 
enum __MIDL___MIDL_itf_msado15_0000_0000_0018
    {	adCompareLessThan	= 0,
	adCompareEqual	= 1,
	adCompareGreaterThan	= 2,
	adCompareNotEqual	= 3,
	adCompareNotComparable	= 4
    } 	CompareEnum;

typedef /* [public][uuid] */  DECLSPEC_UUID("00000546-0000-0010-8000-00AA006D2EA4") 
enum __MIDL___MIDL_itf_msado15_0000_0000_0019
    {	adFilterNone	= 0,
	adFilterPendingRecords	= 1,
	adFilterAffectedRecords	= 2,
	adFilterFetchedRecords	= 3,
	adFilterPredicate	= 4,
	adFilterConflictingRecords	= 5
    } 	FilterGroupEnum;

typedef /* [public][public][public][uuid] */  DECLSPEC_UUID("00000547-0000-0010-8000-00AA006D2EA4") 
enum __MIDL___MIDL_itf_msado15_0000_0000_0020
    {	adSearchForward	= 1,
	adSearchBackward	= -1
    } 	SearchDirectionEnum;

typedef /* [public] */ SearchDirectionEnum SearchDirection;

typedef /* [public][public][public][uuid] */  DECLSPEC_UUID("00000548-0000-0010-8000-00AA006D2EA4") 
enum __MIDL___MIDL_itf_msado15_0000_0000_0021
    {	adPersistADTG	= 0,
	adPersistXML	= 1
    } 	PersistFormatEnum;

typedef /* [public][public][uuid] */  DECLSPEC_UUID("00000549-0000-0010-8000-00AA006D2EA4") 
enum __MIDL___MIDL_itf_msado15_0000_0000_0022
    {	adClipString	= 2
    } 	StringFormatEnum;

typedef /* [public][uuid] */  DECLSPEC_UUID("00000520-0000-0010-8000-00AA006D2EA4") 
enum __MIDL___MIDL_itf_msado15_0000_0000_0023
    {	adPromptAlways	= 1,
	adPromptComplete	= 2,
	adPromptCompleteRequired	= 3,
	adPromptNever	= 4
    } 	ConnectPromptEnum;

typedef /* [public][public][public][public][public][public][public][public][public][uuid] */  DECLSPEC_UUID("00000521-0000-0010-8000-00AA006D2EA4") 
enum __MIDL___MIDL_itf_msado15_0000_0000_0024
    {	adModeUnknown	= 0,
	adModeRead	= 1,
	adModeWrite	= 2,
	adModeReadWrite	= 3,
	adModeShareDenyRead	= 4,
	adModeShareDenyWrite	= 8,
	adModeShareExclusive	= 12,
	adModeShareDenyNone	= 16,
	adModeRecursive	= 4194304
    } 	ConnectModeEnum;

typedef /* [public][public][public][uuid] */  DECLSPEC_UUID("00000523-0000-0010-8000-00AA006D2EA4") 
enum __MIDL___MIDL_itf_msado15_0000_0000_0025
    {	adXactUnspecified	= -1,
	adXactChaos	= 16,
	adXactReadUncommitted	= 256,
	adXactBrowse	= 256,
	adXactCursorStability	= 4096,
	adXactReadCommitted	= 4096,
	adXactRepeatableRead	= 65536,
	adXactSerializable	= 1048576,
	adXactIsolated	= 1048576
    } 	IsolationLevelEnum;

typedef /* [public][uuid] */  DECLSPEC_UUID("00000524-0000-0010-8000-00AA006D2EA4") 
enum __MIDL___MIDL_itf_msado15_0000_0000_0026
    {	adXactCommitRetaining	= 131072,
	adXactAbortRetaining	= 262144,
	adXactAsyncPhaseOne	= 524288,
	adXactSyncPhaseOne	= 1048576
    } 	XactAttributeEnum;

typedef /* [public][uuid] */  DECLSPEC_UUID("00000529-0000-0010-8000-00AA006D2EA4") 
enum __MIDL___MIDL_itf_msado15_0000_0000_0027
    {	adPropNotSupported	= 0,
	adPropRequired	= 1,
	adPropOptional	= 2,
	adPropRead	= 512,
	adPropWrite	= 1024
    } 	PropertyAttributesEnum;

typedef /* [public][uuid] */  DECLSPEC_UUID("0000052A-0000-0010-8000-00AA006D2EA4") 
enum __MIDL___MIDL_itf_msado15_0000_0000_0028
    {	adErrInvalidArgument	= 3001,
	adErrOpeningFile	= 3002,
	adErrReadFile	= 3003,
	adErrWriteFile	= 3004,
	adErrNoCurrentRecord	= 3021,
	adErrIllegalOperation	= 3219,
	adErrCantChangeProvider	= 3220,
	adErrInTransaction	= 3246,
	adErrFeatureNotAvailable	= 3251,
	adErrItemNotFound	= 3265,
	adErrObjectInCollection	= 3367,
	adErrObjectNotSet	= 3420,
	adErrDataConversion	= 3421,
	adErrObjectClosed	= 3704,
	adErrObjectOpen	= 3705,
	adErrProviderNotFound	= 3706,
	adErrBoundToCommand	= 3707,
	adErrInvalidParamInfo	= 3708,
	adErrInvalidConnection	= 3709,
	adErrNotReentrant	= 3710,
	adErrStillExecuting	= 3711,
	adErrOperationCancelled	= 3712,
	adErrStillConnecting	= 3713,
	adErrInvalidTransaction	= 3714,
	adErrNotExecuting	= 3715,
	adErrUnsafeOperation	= 3716,
	adWrnSecurityDialog	= 3717,
	adWrnSecurityDialogHeader	= 3718,
	adErrIntegrityViolation	= 3719,
	adErrPermissionDenied	= 3720,
	adErrDataOverflow	= 3721,
	adErrSchemaViolation	= 3722,
	adErrSignMismatch	= 3723,
	adErrCantConvertvalue	= 3724,
	adErrCantCreate	= 3725,
	adErrColumnNotOnThisRow	= 3726,
	adErrURLDoesNotExist	= 3727,
	adErrTreePermissionDenied	= 3728,
	adErrInvalidURL	= 3729,
	adErrResourceLocked	= 3730,
	adErrResourceExists	= 3731,
	adErrCannotComplete	= 3732,
	adErrVolumeNotFound	= 3733,
	adErrOutOfSpace	= 3734,
	adErrResourceOutOfScope	= 3735,
	adErrUnavailable	= 3736,
	adErrURLNamedRowDoesNotExist	= 3737,
	adErrDelResOutOfScope	= 3738,
	adErrPropInvalidColumn	= 3739,
	adErrPropInvalidOption	= 3740,
	adErrPropInvalidValue	= 3741,
	adErrPropConflicting	= 3742,
	adErrPropNotAllSettable	= 3743,
	adErrPropNotSet	= 3744,
	adErrPropNotSettable	= 3745,
	adErrPropNotSupported	= 3746,
	adErrCatalogNotSet	= 3747,
	adErrCantChangeConnection	= 3748,
	adErrFieldsUpdateFailed	= 3749,
	adErrDenyNotSupported	= 3750,
	adErrDenyTypeNotSupported	= 3751,
	adErrProviderNotSpecified	= 3753,
	adErrConnectionStringTooLong	= 3754
    } 	ErrorValueEnum;

typedef /* [public][uuid] */  DECLSPEC_UUID("0000052B-0000-0010-8000-00AA006D2EA4") 
enum __MIDL___MIDL_itf_msado15_0000_0000_0029
    {	adParamSigned	= 16,
	adParamNullable	= 64,
	adParamLong	= 128
    } 	ParameterAttributesEnum;

typedef /* [public][public][public][public][uuid] */  DECLSPEC_UUID("0000052C-0000-0010-8000-00AA006D2EA4") 
enum __MIDL___MIDL_itf_msado15_0000_0000_0030
    {	adParamUnknown	= 0,
	adParamInput	= 1,
	adParamOutput	= 2,
	adParamInputOutput	= 3,
	adParamReturnValue	= 4
    } 	ParameterDirectionEnum;

typedef /* [public][public][public][uuid] */  DECLSPEC_UUID("0000052E-0000-0010-8000-00AA006D2EA4") 
enum __MIDL___MIDL_itf_msado15_0000_0000_0031
    {	adCmdUnspecified	= -1,
	adCmdUnknown	= 8,
	adCmdText	= 1,
	adCmdTable	= 2,
	adCmdStoredProc	= 4,
	adCmdFile	= 256,
	adCmdTableDirect	= 512
    } 	CommandTypeEnum;

typedef /* [public][public][public][public][public][public][public][public][public][public][public][public][public][public][public][public][public][public][public][public][public][public][public][public][public][public][public][public][public][public][public][public][public][public][public][public][public][public][public][public][public][uuid] */  DECLSPEC_UUID("00000530-0000-0010-8000-00AA006D2EA4") 
enum __MIDL___MIDL_itf_msado15_0000_0000_0032
    {	adStatusOK	= 1,
	adStatusErrorsOccurred	= 2,
	adStatusCantDeny	= 3,
	adStatusCancel	= 4,
	adStatusUnwantedEvent	= 5
    } 	EventStatusEnum;

typedef /* [public][public][public][public][public][public][public][public][public][public][public][public][public][uuid] */  DECLSPEC_UUID("00000531-0000-0010-8000-00AA006D2EA4") 
enum __MIDL___MIDL_itf_msado15_0000_0000_0033
    {	adRsnAddNew	= 1,
	adRsnDelete	= 2,
	adRsnUpdate	= 3,
	adRsnUndoUpdate	= 4,
	adRsnUndoAddNew	= 5,
	adRsnUndoDelete	= 6,
	adRsnRequery	= 7,
	adRsnResynch	= 8,
	adRsnClose	= 9,
	adRsnMove	= 10,
	adRsnFirstChange	= 11,
	adRsnMoveFirst	= 12,
	adRsnMoveNext	= 13,
	adRsnMovePrevious	= 14,
	adRsnMoveLast	= 15
    } 	EventReasonEnum;

typedef /* [public][public][uuid] */  DECLSPEC_UUID("00000533-0000-0010-8000-00AA006D2EA4") 
enum __MIDL___MIDL_itf_msado15_0000_0000_0034
    {	adSchemaProviderSpecific	= -1,
	adSchemaAsserts	= 0,
	adSchemaCatalogs	= 1,
	adSchemaCharacterSets	= 2,
	adSchemaCollations	= 3,
	adSchemaColumns	= 4,
	adSchemaCheckConstraints	= 5,
	adSchemaConstraintColumnUsage	= 6,
	adSchemaConstraintTableUsage	= 7,
	adSchemaKeyColumnUsage	= 8,
	adSchemaReferentialContraints	= 9,
	adSchemaReferentialConstraints	= 9,
	adSchemaTableConstraints	= 10,
	adSchemaColumnsDomainUsage	= 11,
	adSchemaIndexes	= 12,
	adSchemaColumnPrivileges	= 13,
	adSchemaTablePrivileges	= 14,
	adSchemaUsagePrivileges	= 15,
	adSchemaProcedures	= 16,
	adSchemaSchemata	= 17,
	adSchemaSQLLanguages	= 18,
	adSchemaStatistics	= 19,
	adSchemaTables	= 20,
	adSchemaTranslations	= 21,
	adSchemaProviderTypes	= 22,
	adSchemaViews	= 23,
	adSchemaViewColumnUsage	= 24,
	adSchemaViewTableUsage	= 25,
	adSchemaProcedureParameters	= 26,
	adSchemaForeignKeys	= 27,
	adSchemaPrimaryKeys	= 28,
	adSchemaProcedureColumns	= 29,
	adSchemaDBInfoKeywords	= 30,
	adSchemaDBInfoLiterals	= 31,
	adSchemaCubes	= 32,
	adSchemaDimensions	= 33,
	adSchemaHierarchies	= 34,
	adSchemaLevels	= 35,
	adSchemaMeasures	= 36,
	adSchemaProperties	= 37,
	adSchemaMembers	= 38,
	adSchemaTrustees	= 39,
	adSchemaFunctions	= 40,
	adSchemaActions	= 41,
	adSchemaCommands	= 42,
	adSchemaSets	= 43
    } 	SchemaEnum;

typedef /* [public][public][uuid] */  DECLSPEC_UUID("00000552-0000-0010-8000-00AA006D2EA4") 
enum __MIDL___MIDL_itf_msado15_0000_0000_0035
    {	adSeekFirstEQ	= 1,
	adSeekLastEQ	= 2,
	adSeekAfterEQ	= 4,
	adSeekAfter	= 8,
	adSeekBeforeEQ	= 16,
	adSeekBefore	= 32
    } 	SeekEnum;

typedef /* [public][uuid] */  DECLSPEC_UUID("0000054A-0000-0010-8000-00AA006D2EA4") 
enum __MIDL___MIDL_itf_msado15_0000_0000_0036
    {	adCriteriaKey	= 0,
	adCriteriaAllCols	= 1,
	adCriteriaUpdCols	= 2,
	adCriteriaTimeStamp	= 3
    } 	ADCPROP_UPDATECRITERIA_ENUM;

typedef /* [public][uuid] */  DECLSPEC_UUID("0000054B-0000-0010-8000-00AA006D2EA4") 
enum __MIDL___MIDL_itf_msado15_0000_0000_0037
    {	adPriorityLowest	= 1,
	adPriorityBelowNormal	= 2,
	adPriorityNormal	= 3,
	adPriorityAboveNormal	= 4,
	adPriorityHighest	= 5
    } 	ADCPROP_ASYNCTHREADPRIORITY_ENUM;

typedef /* [public][uuid] */  DECLSPEC_UUID("00000553-0000-0010-8000-00AA006D2EA4") 
enum __MIDL___MIDL_itf_msado15_0000_0000_0038
    {	adResyncNone	= 0,
	adResyncAutoIncrement	= 1,
	adResyncConflicts	= 2,
	adResyncUpdates	= 4,
	adResyncInserts	= 8,
	adResyncAll	= 15
    } 	CEResyncEnum;

typedef /* [public][uuid] */  DECLSPEC_UUID("00000554-0000-0010-8000-00AA006D2EA4") 
enum __MIDL___MIDL_itf_msado15_0000_0000_0039
    {	adRecalcUpFront	= 0,
	adRecalcAlways	= 1
    } 	ADCPROP_AUTORECALC_ENUM;

typedef /* [uuid] */  DECLSPEC_UUID("0000057E-0000-0010-8000-00AA006D2EA4") 
enum FieldStatusEnum
    {	adFieldOK	= 0,
	adFieldCantConvertValue	= 2,
	adFieldIsNull	= 3,
	adFieldTruncated	= 4,
	adFieldSignMismatch	= 5,
	adFieldDataOverflow	= 6,
	adFieldCantCreate	= 7,
	adFieldUnavailable	= 8,
	adFieldPermissionDenied	= 9,
	adFieldIntegrityViolation	= 10,
	adFieldSchemaViolation	= 11,
	adFieldBadStatus	= 12,
	adFieldDefault	= 13,
	adFieldIgnore	= 15,
	adFieldDoesNotExist	= 16,
	adFieldInvalidURL	= 17,
	adFieldResourceLocked	= 18,
	adFieldResourceExists	= 19,
	adFieldCannotComplete	= 20,
	adFieldVolumeNotFound	= 21,
	adFieldOutOfSpace	= 22,
	adFieldCannotDeleteSource	= 23,
	adFieldReadOnly	= 24,
	adFieldResourceOutOfScope	= 25,
	adFieldAlreadyExists	= 26,
	adFieldPendingInsert	= 0x10000,
	adFieldPendingDelete	= 0x20000,
	adFieldPendingChange	= 0x40000,
	adFieldPendingUnknown	= 0x80000,
	adFieldPendingUnknownDelete	= 0x100000
    } 	FieldStatusEnum;

typedef /* [public][public][uuid] */  DECLSPEC_UUID("00000570-0000-0010-8000-00AA006D2EA4") 
enum __MIDL___MIDL_itf_msado15_0000_0000_0040
    {	adCreateCollection	= 0x2000,
	adCreateStructDoc	= 0x80000000,
	adCreateNonCollection	= 0,
	adOpenIfExists	= 0x2000000,
	adCreateOverwrite	= 0x4000000,
	adFailIfNotExists	= -1
    } 	RecordCreateOptionsEnum;

typedef /* [public][public][uuid] */  DECLSPEC_UUID("00000571-0000-0010-8000-00AA006D2EA4") 
enum __MIDL___MIDL_itf_msado15_0000_0000_0041
    {	adOpenRecordUnspecified	= -1,
	adOpenSource	= 0x800000,
	adOpenOutput	= 0x800000,
	adOpenAsync	= 0x1000,
	adDelayFetchStream	= 0x4000,
	adDelayFetchFields	= 0x8000,
	adOpenExecuteCommand	= 0x10000
    } 	RecordOpenOptionsEnum;

typedef /* [public][public][uuid] */  DECLSPEC_UUID("00000573-0000-0010-8000-00AA006D2EA4") 
enum __MIDL___MIDL_itf_msado15_0000_0000_0042
    {	adMoveUnspecified	= -1,
	adMoveOverWrite	= 1,
	adMoveDontUpdateLinks	= 2,
	adMoveAllowEmulation	= 4
    } 	MoveRecordOptionsEnum;

typedef /* [public][public][uuid] */  DECLSPEC_UUID("00000574-0000-0010-8000-00AA006D2EA4") 
enum __MIDL___MIDL_itf_msado15_0000_0000_0043
    {	adCopyUnspecified	= -1,
	adCopyOverWrite	= 1,
	adCopyAllowEmulation	= 4,
	adCopyNonRecursive	= 2
    } 	CopyRecordOptionsEnum;

typedef /* [public][public][public][uuid] */  DECLSPEC_UUID("00000576-0000-0010-8000-00AA006D2EA4") 
enum __MIDL___MIDL_itf_msado15_0000_0000_0044
    {	adTypeBinary	= 1,
	adTypeText	= 2
    } 	StreamTypeEnum;

typedef /* [public][public][public][uuid] */  DECLSPEC_UUID("00000577-0000-0010-8000-00AA006D2EA4") 
enum __MIDL___MIDL_itf_msado15_0000_0000_0045
    {	adLF	= 10,
	adCR	= 13,
	adCRLF	= -1
    } 	LineSeparatorEnum;

typedef /* [public][public][uuid] */  DECLSPEC_UUID("0000057A-0000-0010-8000-00AA006D2EA4") 
enum __MIDL___MIDL_itf_msado15_0000_0000_0046
    {	adOpenStreamUnspecified	= -1,
	adOpenStreamAsync	= 1,
	adOpenStreamFromRecord	= 4
    } 	StreamOpenOptionsEnum;

typedef /* [public][public][uuid] */  DECLSPEC_UUID("0000057B-0000-0010-8000-00AA006D2EA4") 
enum __MIDL___MIDL_itf_msado15_0000_0000_0047
    {	adWriteChar	= 0,
	adWriteLine	= 1
    } 	StreamWriteEnum;

typedef /* [public][public][uuid] */  DECLSPEC_UUID("0000057C-0000-0010-8000-00AA006D2EA4") 
enum __MIDL___MIDL_itf_msado15_0000_0000_0048
    {	adSaveCreateNotExist	= 1,
	adSaveCreateOverWrite	= 2
    } 	SaveOptionsEnum;

typedef /* [public] */ 
enum __MIDL___MIDL_itf_msado15_0000_0000_0049
    {	adDefaultStream	= -1,
	adRecordURL	= -2
    } 	FieldEnum;

typedef /* [public] */ 
enum __MIDL___MIDL_itf_msado15_0000_0000_0050
    {	adReadAll	= -1,
	adReadLine	= -2
    } 	StreamReadEnum;

typedef /* [public][public][uuid] */  DECLSPEC_UUID("0000057D-0000-0010-8000-00AA006D2EA4") 
enum __MIDL___MIDL_itf_msado15_0000_0000_0051
    {	adSimpleRecord	= 0,
	adCollectionRecord	= 1,
	adStructDoc	= 2
    } 	RecordTypeEnum;



extern RPC_IF_HANDLE __MIDL_itf_msado15_0000_0000_v0_0_c_ifspec;
extern RPC_IF_HANDLE __MIDL_itf_msado15_0000_0000_v0_0_s_ifspec;

#ifndef ___Collection_INTERFACE_DEFINED__
#define ___Collection_INTERFACE_DEFINED__

/* interface _Collection */
/* [object][oleautomation][nonextensible][dual][uuid] */ 


EXTERN_C const IID IID__Collection;

#if defined(__cplusplus) && !defined(CINTERFACE)
    
    MIDL_INTERFACE("00000512-0000-0010-8000-00AA006D2EA4")
    _Collection : public IDispatch
    {
    public:
        virtual /* [propget][id] */ HRESULT __stdcall get_Count( 
            /* [retval][out] */ long *c) = 0;
        
        virtual /* [restricted][id] */ HRESULT __stdcall _NewEnum( 
            /* [retval][out] */ IUnknown **ppvObject) = 0;
        
        virtual /* [id] */ HRESULT __stdcall Refresh( void) = 0;
        
    };
    
#else 	/* C style interface */

    typedef struct _CollectionVtbl
    {
        BEGIN_INTERFACE
        
        HRESULT ( STDMETHODCALLTYPE *QueryInterface )( 
            _Collection * This,
            /* [in] */ REFIID riid,
            /* [annotation][iid_is][out] */ 
            __RPC__deref_out  void **ppvObject);
        
        ULONG ( STDMETHODCALLTYPE *AddRef )( 
            _Collection * This);
        
        ULONG ( STDMETHODCALLTYPE *Release )( 
            _Collection * This);
        
        HRESULT ( STDMETHODCALLTYPE *GetTypeInfoCount )( 
            _Collection * This,
            /* [out] */ UINT *pctinfo);
        
        HRESULT ( STDMETHODCALLTYPE *GetTypeInfo )( 
            _Collection * This,
            /* [in] */ UINT iTInfo,
            /* [in] */ LCID lcid,
            /* [out] */ ITypeInfo **ppTInfo);
        
        HRESULT ( STDMETHODCALLTYPE *GetIDsOfNames )( 
            _Collection * This,
            /* [in] */ REFIID riid,
            /* [size_is][in] */ LPOLESTR *rgszNames,
            /* [range][in] */ UINT cNames,
            /* [in] */ LCID lcid,
            /* [size_is][out] */ DISPID *rgDispId);
        
        /* [local] */ HRESULT ( STDMETHODCALLTYPE *Invoke )( 
            _Collection * This,
            /* [in] */ DISPID dispIdMember,
            /* [in] */ REFIID riid,
            /* [in] */ LCID lcid,
            /* [in] */ WORD wFlags,
            /* [out][in] */ DISPPARAMS *pDispParams,
            /* [out] */ VARIANT *pVarResult,
            /* [out] */ EXCEPINFO *pExcepInfo,
            /* [out] */ UINT *puArgErr);
        
        /* [propget][id] */ HRESULT ( __stdcall *get_Count )( 
            _Collection * This,
            /* [retval][out] */ long *c);
        
        /* [restricted][id] */ HRESULT ( __stdcall *_NewEnum )( 
            _Collection * This,
            /* [retval][out] */ IUnknown **ppvObject);
        
        /* [id] */ HRESULT ( __stdcall *Refresh )( 
            _Collection * This);
        
        END_INTERFACE
    } _CollectionVtbl;

    interface _Collection
    {
        CONST_VTBL struct _CollectionVtbl *lpVtbl;
    };

    

#ifdef COBJMACROS


#define _Collection_QueryInterface(This,riid,ppvObject)	\
    ( (This)->lpVtbl -> QueryInterface(This,riid,ppvObject) ) 

#define _Collection_AddRef(This)	\
    ( (This)->lpVtbl -> AddRef(This) ) 

#define _Collection_Release(This)	\
    ( (This)->lpVtbl -> Release(This) ) 


#define _Collection_GetTypeInfoCount(This,pctinfo)	\
    ( (This)->lpVtbl -> GetTypeInfoCount(This,pctinfo) ) 

#define _Collection_GetTypeInfo(This,iTInfo,lcid,ppTInfo)	\
    ( (This)->lpVtbl -> GetTypeInfo(This,iTInfo,lcid,ppTInfo) ) 

#define _Collection_GetIDsOfNames(This,riid,rgszNames,cNames,lcid,rgDispId)	\
    ( (This)->lpVtbl -> GetIDsOfNames(This,riid,rgszNames,cNames,lcid,rgDispId) ) 

#define _Collection_Invoke(This,dispIdMember,riid,lcid,wFlags,pDispParams,pVarResult,pExcepInfo,puArgErr)	\
    ( (This)->lpVtbl -> Invoke(This,dispIdMember,riid,lcid,wFlags,pDispParams,pVarResult,pExcepInfo,puArgErr) ) 


#define _Collection_get_Count(This,c)	\
    ( (This)->lpVtbl -> get_Count(This,c) ) 

#define _Collection__NewEnum(This,ppvObject)	\
    ( (This)->lpVtbl -> _NewEnum(This,ppvObject) ) 

#define _Collection_Refresh(This)	\
    ( (This)->lpVtbl -> Refresh(This) ) 

#endif /* COBJMACROS */


#endif 	/* C style interface */




#endif 	/* ___Collection_INTERFACE_DEFINED__ */


#ifndef ___DynaCollection_INTERFACE_DEFINED__
#define ___DynaCollection_INTERFACE_DEFINED__

/* interface _DynaCollection */
/* [object][oleautomation][nonextensible][dual][uuid] */ 


EXTERN_C const IID IID__DynaCollection;

#if defined(__cplusplus) && !defined(CINTERFACE)
    
    MIDL_INTERFACE("00000513-0000-0010-8000-00AA006D2EA4")
    _DynaCollection : public _Collection
    {
    public:
        virtual /* [id] */ HRESULT __stdcall Append( 
            /* [in] */ IDispatch *Object) = 0;
        
        virtual /* [id] */ HRESULT __stdcall Delete( 
            /* [in] */ VARIANT Index) = 0;
        
    };
    
#else 	/* C style interface */

    typedef struct _DynaCollectionVtbl
    {
        BEGIN_INTERFACE
        
        HRESULT ( STDMETHODCALLTYPE *QueryInterface )( 
            _DynaCollection * This,
            /* [in] */ REFIID riid,
            /* [annotation][iid_is][out] */ 
            __RPC__deref_out  void **ppvObject);
        
        ULONG ( STDMETHODCALLTYPE *AddRef )( 
            _DynaCollection * This);
        
        ULONG ( STDMETHODCALLTYPE *Release )( 
            _DynaCollection * This);
        
        HRESULT ( STDMETHODCALLTYPE *GetTypeInfoCount )( 
            _DynaCollection * This,
            /* [out] */ UINT *pctinfo);
        
        HRESULT ( STDMETHODCALLTYPE *GetTypeInfo )( 
            _DynaCollection * This,
            /* [in] */ UINT iTInfo,
            /* [in] */ LCID lcid,
            /* [out] */ ITypeInfo **ppTInfo);
        
        HRESULT ( STDMETHODCALLTYPE *GetIDsOfNames )( 
            _DynaCollection * This,
            /* [in] */ REFIID riid,
            /* [size_is][in] */ LPOLESTR *rgszNames,
            /* [range][in] */ UINT cNames,
            /* [in] */ LCID lcid,
            /* [size_is][out] */ DISPID *rgDispId);
        
        /* [local] */ HRESULT ( STDMETHODCALLTYPE *Invoke )( 
            _DynaCollection * This,
            /* [in] */ DISPID dispIdMember,
            /* [in] */ REFIID riid,
            /* [in] */ LCID lcid,
            /* [in] */ WORD wFlags,
            /* [out][in] */ DISPPARAMS *pDispParams,
            /* [out] */ VARIANT *pVarResult,
            /* [out] */ EXCEPINFO *pExcepInfo,
            /* [out] */ UINT *puArgErr);
        
        /* [propget][id] */ HRESULT ( __stdcall *get_Count )( 
            _DynaCollection * This,
            /* [retval][out] */ long *c);
        
        /* [restricted][id] */ HRESULT ( __stdcall *_NewEnum )( 
            _DynaCollection * This,
            /* [retval][out] */ IUnknown **ppvObject);
        
        /* [id] */ HRESULT ( __stdcall *Refresh )( 
            _DynaCollection * This);
        
        /* [id] */ HRESULT ( __stdcall *Append )( 
            _DynaCollection * This,
            /* [in] */ IDispatch *Object);
        
        /* [id] */ HRESULT ( __stdcall *Delete )( 
            _DynaCollection * This,
            /* [in] */ VARIANT Index);
        
        END_INTERFACE
    } _DynaCollectionVtbl;

    interface _DynaCollection
    {
        CONST_VTBL struct _DynaCollectionVtbl *lpVtbl;
    };

    

#ifdef COBJMACROS


#define _DynaCollection_QueryInterface(This,riid,ppvObject)	\
    ( (This)->lpVtbl -> QueryInterface(This,riid,ppvObject) ) 

#define _DynaCollection_AddRef(This)	\
    ( (This)->lpVtbl -> AddRef(This) ) 

#define _DynaCollection_Release(This)	\
    ( (This)->lpVtbl -> Release(This) ) 


#define _DynaCollection_GetTypeInfoCount(This,pctinfo)	\
    ( (This)->lpVtbl -> GetTypeInfoCount(This,pctinfo) ) 

#define _DynaCollection_GetTypeInfo(This,iTInfo,lcid,ppTInfo)	\
    ( (This)->lpVtbl -> GetTypeInfo(This,iTInfo,lcid,ppTInfo) ) 

#define _DynaCollection_GetIDsOfNames(This,riid,rgszNames,cNames,lcid,rgDispId)	\
    ( (This)->lpVtbl -> GetIDsOfNames(This,riid,rgszNames,cNames,lcid,rgDispId) ) 

#define _DynaCollection_Invoke(This,dispIdMember,riid,lcid,wFlags,pDispParams,pVarResult,pExcepInfo,puArgErr)	\
    ( (This)->lpVtbl -> Invoke(This,dispIdMember,riid,lcid,wFlags,pDispParams,pVarResult,pExcepInfo,puArgErr) ) 


#define _DynaCollection_get_Count(This,c)	\
    ( (This)->lpVtbl -> get_Count(This,c) ) 

#define _DynaCollection__NewEnum(This,ppvObject)	\
    ( (This)->lpVtbl -> _NewEnum(This,ppvObject) ) 

#define _DynaCollection_Refresh(This)	\
    ( (This)->lpVtbl -> Refresh(This) ) 


#define _DynaCollection_Append(This,Object)	\
    ( (This)->lpVtbl -> Append(This,Object) ) 

#define _DynaCollection_Delete(This,Index)	\
    ( (This)->lpVtbl -> Delete(This,Index) ) 

#endif /* COBJMACROS */


#endif 	/* C style interface */




#endif 	/* ___DynaCollection_INTERFACE_DEFINED__ */


#ifndef ___ADO_INTERFACE_DEFINED__
#define ___ADO_INTERFACE_DEFINED__

/* interface _ADO */
/* [object][oleautomation][nonextensible][dual][uuid] */ 


EXTERN_C const IID IID__ADO;

#if defined(__cplusplus) && !defined(CINTERFACE)
    
    MIDL_INTERFACE("00000534-0000-0010-8000-00AA006D2EA4")
    _ADO : public IDispatch
    {
    public:
        virtual /* [propget][id] */ HRESULT __stdcall get_Properties( 
            /* [retval][out] */ Properties **ppvObject) = 0;
        
    };
    
#else 	/* C style interface */

    typedef struct _ADOVtbl
    {
        BEGIN_INTERFACE
        
        HRESULT ( STDMETHODCALLTYPE *QueryInterface )( 
            _ADO * This,
            /* [in] */ REFIID riid,
            /* [annotation][iid_is][out] */ 
            __RPC__deref_out  void **ppvObject);
        
        ULONG ( STDMETHODCALLTYPE *AddRef )( 
            _ADO * This);
        
        ULONG ( STDMETHODCALLTYPE *Release )( 
            _ADO * This);
        
        HRESULT ( STDMETHODCALLTYPE *GetTypeInfoCount )( 
            _ADO * This,
            /* [out] */ UINT *pctinfo);
        
        HRESULT ( STDMETHODCALLTYPE *GetTypeInfo )( 
            _ADO * This,
            /* [in] */ UINT iTInfo,
            /* [in] */ LCID lcid,
            /* [out] */ ITypeInfo **ppTInfo);
        
        HRESULT ( STDMETHODCALLTYPE *GetIDsOfNames )( 
            _ADO * This,
            /* [in] */ REFIID riid,
            /* [size_is][in] */ LPOLESTR *rgszNames,
            /* [range][in] */ UINT cNames,
            /* [in] */ LCID lcid,
            /* [size_is][out] */ DISPID *rgDispId);
        
        /* [local] */ HRESULT ( STDMETHODCALLTYPE *Invoke )( 
            _ADO * This,
            /* [in] */ DISPID dispIdMember,
            /* [in] */ REFIID riid,
            /* [in] */ LCID lcid,
            /* [in] */ WORD wFlags,
            /* [out][in] */ DISPPARAMS *pDispParams,
            /* [out] */ VARIANT *pVarResult,
            /* [out] */ EXCEPINFO *pExcepInfo,
            /* [out] */ UINT *puArgErr);
        
        /* [propget][id] */ HRESULT ( __stdcall *get_Properties )( 
            _ADO * This,
            /* [retval][out] */ Properties **ppvObject);
        
        END_INTERFACE
    } _ADOVtbl;

    interface _ADO
    {
        CONST_VTBL struct _ADOVtbl *lpVtbl;
    };

    

#ifdef COBJMACROS


#define _ADO_QueryInterface(This,riid,ppvObject)	\
    ( (This)->lpVtbl -> QueryInterface(This,riid,ppvObject) ) 

#define _ADO_AddRef(This)	\
    ( (This)->lpVtbl -> AddRef(This) ) 

#define _ADO_Release(This)	\
    ( (This)->lpVtbl -> Release(This) ) 


#define _ADO_GetTypeInfoCount(This,pctinfo)	\
    ( (This)->lpVtbl -> GetTypeInfoCount(This,pctinfo) ) 

#define _ADO_GetTypeInfo(This,iTInfo,lcid,ppTInfo)	\
    ( (This)->lpVtbl -> GetTypeInfo(This,iTInfo,lcid,ppTInfo) ) 

#define _ADO_GetIDsOfNames(This,riid,rgszNames,cNames,lcid,rgDispId)	\
    ( (This)->lpVtbl -> GetIDsOfNames(This,riid,rgszNames,cNames,lcid,rgDispId) ) 

#define _ADO_Invoke(This,dispIdMember,riid,lcid,wFlags,pDispParams,pVarResult,pExcepInfo,puArgErr)	\
    ( (This)->lpVtbl -> Invoke(This,dispIdMember,riid,lcid,wFlags,pDispParams,pVarResult,pExcepInfo,puArgErr) ) 


#define _ADO_get_Properties(This,ppvObject)	\
    ( (This)->lpVtbl -> get_Properties(This,ppvObject) ) 

#endif /* COBJMACROS */


#endif 	/* C style interface */




#endif 	/* ___ADO_INTERFACE_DEFINED__ */


#ifndef __Properties_INTERFACE_DEFINED__
#define __Properties_INTERFACE_DEFINED__

/* interface Properties */
/* [object][oleautomation][nonextensible][dual][uuid] */ 


EXTERN_C const IID IID_Properties;

#if defined(__cplusplus) && !defined(CINTERFACE)
    
    MIDL_INTERFACE("00000504-0000-0010-8000-00AA006D2EA4")
    Properties : public _Collection
    {
    public:
        virtual /* [propget][id] */ HRESULT __stdcall get_Item( 
            /* [in] */ VARIANT Index,
            /* [retval][out] */ Property **ppvObject) = 0;
        
    };
    
#else 	/* C style interface */

    typedef struct PropertiesVtbl
    {
        BEGIN_INTERFACE
        
        HRESULT ( STDMETHODCALLTYPE *QueryInterface )( 
            Properties * This,
            /* [in] */ REFIID riid,
            /* [annotation][iid_is][out] */ 
            __RPC__deref_out  void **ppvObject);
        
        ULONG ( STDMETHODCALLTYPE *AddRef )( 
            Properties * This);
        
        ULONG ( STDMETHODCALLTYPE *Release )( 
            Properties * This);
        
        HRESULT ( STDMETHODCALLTYPE *GetTypeInfoCount )( 
            Properties * This,
            /* [out] */ UINT *pctinfo);
        
        HRESULT ( STDMETHODCALLTYPE *GetTypeInfo )( 
            Properties * This,
            /* [in] */ UINT iTInfo,
            /* [in] */ LCID lcid,
            /* [out] */ ITypeInfo **ppTInfo);
        
        HRESULT ( STDMETHODCALLTYPE *GetIDsOfNames )( 
            Properties * This,
            /* [in] */ REFIID riid,
            /* [size_is][in] */ LPOLESTR *rgszNames,
            /* [range][in] */ UINT cNames,
            /* [in] */ LCID lcid,
            /* [size_is][out] */ DISPID *rgDispId);
        
        /* [local] */ HRESULT ( STDMETHODCALLTYPE *Invoke )( 
            Properties * This,
            /* [in] */ DISPID dispIdMember,
            /* [in] */ REFIID riid,
            /* [in] */ LCID lcid,
            /* [in] */ WORD wFlags,
            /* [out][in] */ DISPPARAMS *pDispParams,
            /* [out] */ VARIANT *pVarResult,
            /* [out] */ EXCEPINFO *pExcepInfo,
            /* [out] */ UINT *puArgErr);
        
        /* [propget][id] */ HRESULT ( __stdcall *get_Count )( 
            Properties * This,
            /* [retval][out] */ long *c);
        
        /* [restricted][id] */ HRESULT ( __stdcall *_NewEnum )( 
            Properties * This,
            /* [retval][out] */ IUnknown **ppvObject);
        
        /* [id] */ HRESULT ( __stdcall *Refresh )( 
            Properties * This);
        
        /* [propget][id] */ HRESULT ( __stdcall *get_Item )( 
            Properties * This,
            /* [in] */ VARIANT Index,
            /* [retval][out] */ Property **ppvObject);
        
        END_INTERFACE
    } PropertiesVtbl;

    interface Properties
    {
        CONST_VTBL struct PropertiesVtbl *lpVtbl;
    };

    

#ifdef COBJMACROS


#define Properties_QueryInterface(This,riid,ppvObject)	\
    ( (This)->lpVtbl -> QueryInterface(This,riid,ppvObject) ) 

#define Properties_AddRef(This)	\
    ( (This)->lpVtbl -> AddRef(This) ) 

#define Properties_Release(This)	\
    ( (This)->lpVtbl -> Release(This) ) 


#define Properties_GetTypeInfoCount(This,pctinfo)	\
    ( (This)->lpVtbl -> GetTypeInfoCount(This,pctinfo) ) 

#define Properties_GetTypeInfo(This,iTInfo,lcid,ppTInfo)	\
    ( (This)->lpVtbl -> GetTypeInfo(This,iTInfo,lcid,ppTInfo) ) 

#define Properties_GetIDsOfNames(This,riid,rgszNames,cNames,lcid,rgDispId)	\
    ( (This)->lpVtbl -> GetIDsOfNames(This,riid,rgszNames,cNames,lcid,rgDispId) ) 

#define Properties_Invoke(This,dispIdMember,riid,lcid,wFlags,pDispParams,pVarResult,pExcepInfo,puArgErr)	\
    ( (This)->lpVtbl -> Invoke(This,dispIdMember,riid,lcid,wFlags,pDispParams,pVarResult,pExcepInfo,puArgErr) ) 


#define Properties_get_Count(This,c)	\
    ( (This)->lpVtbl -> get_Count(This,c) ) 

#define Properties__NewEnum(This,ppvObject)	\
    ( (This)->lpVtbl -> _NewEnum(This,ppvObject) ) 

#define Properties_Refresh(This)	\
    ( (This)->lpVtbl -> Refresh(This) ) 


#define Properties_get_Item(This,Index,ppvObject)	\
    ( (This)->lpVtbl -> get_Item(This,Index,ppvObject) ) 

#endif /* COBJMACROS */


#endif 	/* C style interface */




#endif 	/* __Properties_INTERFACE_DEFINED__ */


#ifndef __Property_INTERFACE_DEFINED__
#define __Property_INTERFACE_DEFINED__

/* interface Property */
/* [object][oleautomation][nonextensible][dual][uuid] */ 


EXTERN_C const IID IID_Property;

#if defined(__cplusplus) && !defined(CINTERFACE)
    
    MIDL_INTERFACE("00000503-0000-0010-8000-00AA006D2EA4")
    Property : public IDispatch
    {
    public:
        virtual /* [propget][id] */ HRESULT __stdcall get_Value( 
            /* [retval][out] */ VARIANT *pval) = 0;
        
        virtual /* [propput][id] */ HRESULT __stdcall put_Value( 
            /* [in] */ VARIANT pval) = 0;
        
        virtual /* [propget][id] */ HRESULT __stdcall get_Name( 
            /* [retval][out] */ BSTR *pbstr) = 0;
        
        virtual /* [propget][id] */ HRESULT __stdcall get_Type( 
            /* [retval][out] */ DataTypeEnum *ptype) = 0;
        
        virtual /* [propget][id] */ HRESULT __stdcall get_Attributes( 
            /* [retval][out] */ long *plAttributes) = 0;
        
        virtual /* [propput][id] */ HRESULT __stdcall put_Attributes( 
            /* [in] */ long plAttributes) = 0;
        
    };
    
#else 	/* C style interface */

    typedef struct PropertyVtbl
    {
        BEGIN_INTERFACE
        
        HRESULT ( STDMETHODCALLTYPE *QueryInterface )( 
            Property * This,
            /* [in] */ REFIID riid,
            /* [annotation][iid_is][out] */ 
            __RPC__deref_out  void **ppvObject);
        
        ULONG ( STDMETHODCALLTYPE *AddRef )( 
            Property * This);
        
        ULONG ( STDMETHODCALLTYPE *Release )( 
            Property * This);
        
        HRESULT ( STDMETHODCALLTYPE *GetTypeInfoCount )( 
            Property * This,
            /* [out] */ UINT *pctinfo);
        
        HRESULT ( STDMETHODCALLTYPE *GetTypeInfo )( 
            Property * This,
            /* [in] */ UINT iTInfo,
            /* [in] */ LCID lcid,
            /* [out] */ ITypeInfo **ppTInfo);
        
        HRESULT ( STDMETHODCALLTYPE *GetIDsOfNames )( 
            Property * This,
            /* [in] */ REFIID riid,
            /* [size_is][in] */ LPOLESTR *rgszNames,
            /* [range][in] */ UINT cNames,
            /* [in] */ LCID lcid,
            /* [size_is][out] */ DISPID *rgDispId);
        
        /* [local] */ HRESULT ( STDMETHODCALLTYPE *Invoke )( 
            Property * This,
            /* [in] */ DISPID dispIdMember,
            /* [in] */ REFIID riid,
            /* [in] */ LCID lcid,
            /* [in] */ WORD wFlags,
            /* [out][in] */ DISPPARAMS *pDispParams,
            /* [out] */ VARIANT *pVarResult,
            /* [out] */ EXCEPINFO *pExcepInfo,
            /* [out] */ UINT *puArgErr);
        
        /* [propget][id] */ HRESULT ( __stdcall *get_Value )( 
            Property * This,
            /* [retval][out] */ VARIANT *pval);
        
        /* [propput][id] */ HRESULT ( __stdcall *put_Value )( 
            Property * This,
            /* [in] */ VARIANT pval);
        
        /* [propget][id] */ HRESULT ( __stdcall *get_Name )( 
            Property * This,
            /* [retval][out] */ BSTR *pbstr);
        
        /* [propget][id] */ HRESULT ( __stdcall *get_Type )( 
            Property * This,
            /* [retval][out] */ DataTypeEnum *ptype);
        
        /* [propget][id] */ HRESULT ( __stdcall *get_Attributes )( 
            Property * This,
            /* [retval][out] */ long *plAttributes);
        
        /* [propput][id] */ HRESULT ( __stdcall *put_Attributes )( 
            Property * This,
            /* [in] */ long plAttributes);
        
        END_INTERFACE
    } PropertyVtbl;

    interface Property
    {
        CONST_VTBL struct PropertyVtbl *lpVtbl;
    };

    

#ifdef COBJMACROS


#define Property_QueryInterface(This,riid,ppvObject)	\
    ( (This)->lpVtbl -> QueryInterface(This,riid,ppvObject) ) 

#define Property_AddRef(This)	\
    ( (This)->lpVtbl -> AddRef(This) ) 

#define Property_Release(This)	\
    ( (This)->lpVtbl -> Release(This) ) 


#define Property_GetTypeInfoCount(This,pctinfo)	\
    ( (This)->lpVtbl -> GetTypeInfoCount(This,pctinfo) ) 

#define Property_GetTypeInfo(This,iTInfo,lcid,ppTInfo)	\
    ( (This)->lpVtbl -> GetTypeInfo(This,iTInfo,lcid,ppTInfo) ) 

#define Property_GetIDsOfNames(This,riid,rgszNames,cNames,lcid,rgDispId)	\
    ( (This)->lpVtbl -> GetIDsOfNames(This,riid,rgszNames,cNames,lcid,rgDispId) ) 

#define Property_Invoke(This,dispIdMember,riid,lcid,wFlags,pDispParams,pVarResult,pExcepInfo,puArgErr)	\
    ( (This)->lpVtbl -> Invoke(This,dispIdMember,riid,lcid,wFlags,pDispParams,pVarResult,pExcepInfo,puArgErr) ) 


#define Property_get_Value(This,pval)	\
    ( (This)->lpVtbl -> get_Value(This,pval) ) 

#define Property_put_Value(This,pval)	\
    ( (This)->lpVtbl -> put_Value(This,pval) ) 

#define Property_get_Name(This,pbstr)	\
    ( (This)->lpVtbl -> get_Name(This,pbstr) ) 

#define Property_get_Type(This,ptype)	\
    ( (This)->lpVtbl -> get_Type(This,ptype) ) 

#define Property_get_Attributes(This,plAttributes)	\
    ( (This)->lpVtbl -> get_Attributes(This,plAttributes) ) 

#define Property_put_Attributes(This,plAttributes)	\
    ( (This)->lpVtbl -> put_Attributes(This,plAttributes) ) 

#endif /* COBJMACROS */


#endif 	/* C style interface */




#endif 	/* __Property_INTERFACE_DEFINED__ */


#ifndef __Error_INTERFACE_DEFINED__
#define __Error_INTERFACE_DEFINED__

/* interface Error */
/* [object][oleautomation][nonextensible][dual][uuid] */ 


EXTERN_C const IID IID_Error;

#if defined(__cplusplus) && !defined(CINTERFACE)
    
    MIDL_INTERFACE("00000500-0000-0010-8000-00AA006D2EA4")
    Error : public IDispatch
    {
    public:
        virtual /* [propget][id] */ HRESULT __stdcall get_Number( 
            /* [retval][out] */ long *pl) = 0;
        
        virtual /* [propget][id] */ HRESULT __stdcall get_Source( 
            /* [retval][out] */ BSTR *pbstr) = 0;
        
        virtual /* [propget][id] */ HRESULT __stdcall get_Description( 
            /* [retval][out] */ BSTR *pbstr) = 0;
        
        virtual /* [propget][id] */ HRESULT __stdcall get_HelpFile( 
            /* [retval][out] */ BSTR *pbstr) = 0;
        
        virtual /* [propget][id] */ HRESULT __stdcall get_HelpContext( 
            /* [retval][out] */ long *pl) = 0;
        
        virtual /* [propget][id] */ HRESULT __stdcall get_SQLState( 
            /* [retval][out] */ BSTR *pbstr) = 0;
        
        virtual /* [propget][id] */ HRESULT __stdcall get_NativeError( 
            /* [retval][out] */ long *pl) = 0;
        
    };
    
#else 	/* C style interface */

    typedef struct ErrorVtbl
    {
        BEGIN_INTERFACE
        
        HRESULT ( STDMETHODCALLTYPE *QueryInterface )( 
            Error * This,
            /* [in] */ REFIID riid,
            /* [annotation][iid_is][out] */ 
            __RPC__deref_out  void **ppvObject);
        
        ULONG ( STDMETHODCALLTYPE *AddRef )( 
            Error * This);
        
        ULONG ( STDMETHODCALLTYPE *Release )( 
            Error * This);
        
        HRESULT ( STDMETHODCALLTYPE *GetTypeInfoCount )( 
            Error * This,
            /* [out] */ UINT *pctinfo);
        
        HRESULT ( STDMETHODCALLTYPE *GetTypeInfo )( 
            Error * This,
            /* [in] */ UINT iTInfo,
            /* [in] */ LCID lcid,
            /* [out] */ ITypeInfo **ppTInfo);
        
        HRESULT ( STDMETHODCALLTYPE *GetIDsOfNames )( 
            Error * This,
            /* [in] */ REFIID riid,
            /* [size_is][in] */ LPOLESTR *rgszNames,
            /* [range][in] */ UINT cNames,
            /* [in] */ LCID lcid,
            /* [size_is][out] */ DISPID *rgDispId);
        
        /* [local] */ HRESULT ( STDMETHODCALLTYPE *Invoke )( 
            Error * This,
            /* [in] */ DISPID dispIdMember,
            /* [in] */ REFIID riid,
            /* [in] */ LCID lcid,
            /* [in] */ WORD wFlags,
            /* [out][in] */ DISPPARAMS *pDispParams,
            /* [out] */ VARIANT *pVarResult,
            /* [out] */ EXCEPINFO *pExcepInfo,
            /* [out] */ UINT *puArgErr);
        
        /* [propget][id] */ HRESULT ( __stdcall *get_Number )( 
            Error * This,
            /* [retval][out] */ long *pl);
        
        /* [propget][id] */ HRESULT ( __stdcall *get_Source )( 
            Error * This,
            /* [retval][out] */ BSTR *pbstr);
        
        /* [propget][id] */ HRESULT ( __stdcall *get_Description )( 
            Error * This,
            /* [retval][out] */ BSTR *pbstr);
        
        /* [propget][id] */ HRESULT ( __stdcall *get_HelpFile )( 
            Error * This,
            /* [retval][out] */ BSTR *pbstr);
        
        /* [propget][id] */ HRESULT ( __stdcall *get_HelpContext )( 
            Error * This,
            /* [retval][out] */ long *pl);
        
        /* [propget][id] */ HRESULT ( __stdcall *get_SQLState )( 
            Error * This,
            /* [retval][out] */ BSTR *pbstr);
        
        /* [propget][id] */ HRESULT ( __stdcall *get_NativeError )( 
            Error * This,
            /* [retval][out] */ long *pl);
        
        END_INTERFACE
    } ErrorVtbl;

    interface Error
    {
        CONST_VTBL struct ErrorVtbl *lpVtbl;
    };

    

#ifdef COBJMACROS


#define Error_QueryInterface(This,riid,ppvObject)	\
    ( (This)->lpVtbl -> QueryInterface(This,riid,ppvObject) ) 

#define Error_AddRef(This)	\
    ( (This)->lpVtbl -> AddRef(This) ) 

#define Error_Release(This)	\
    ( (This)->lpVtbl -> Release(This) ) 


#define Error_GetTypeInfoCount(This,pctinfo)	\
    ( (This)->lpVtbl -> GetTypeInfoCount(This,pctinfo) ) 

#define Error_GetTypeInfo(This,iTInfo,lcid,ppTInfo)	\
    ( (This)->lpVtbl -> GetTypeInfo(This,iTInfo,lcid,ppTInfo) ) 

#define Error_GetIDsOfNames(This,riid,rgszNames,cNames,lcid,rgDispId)	\
    ( (This)->lpVtbl -> GetIDsOfNames(This,riid,rgszNames,cNames,lcid,rgDispId) ) 

#define Error_Invoke(This,dispIdMember,riid,lcid,wFlags,pDispParams,pVarResult,pExcepInfo,puArgErr)	\
    ( (This)->lpVtbl -> Invoke(This,dispIdMember,riid,lcid,wFlags,pDispParams,pVarResult,pExcepInfo,puArgErr) ) 


#define Error_get_Number(This,pl)	\
    ( (This)->lpVtbl -> get_Number(This,pl) ) 

#define Error_get_Source(This,pbstr)	\
    ( (This)->lpVtbl -> get_Source(This,pbstr) ) 

#define Error_get_Description(This,pbstr)	\
    ( (This)->lpVtbl -> get_Description(This,pbstr) ) 

#define Error_get_HelpFile(This,pbstr)	\
    ( (This)->lpVtbl -> get_HelpFile(This,pbstr) ) 

#define Error_get_HelpContext(This,pl)	\
    ( (This)->lpVtbl -> get_HelpContext(This,pl) ) 

#define Error_get_SQLState(This,pbstr)	\
    ( (This)->lpVtbl -> get_SQLState(This,pbstr) ) 

#define Error_get_NativeError(This,pl)	\
    ( (This)->lpVtbl -> get_NativeError(This,pl) ) 

#endif /* COBJMACROS */


#endif 	/* C style interface */




#endif 	/* __Error_INTERFACE_DEFINED__ */


#ifndef __Errors_INTERFACE_DEFINED__
#define __Errors_INTERFACE_DEFINED__

/* interface Errors */
/* [object][oleautomation][nonextensible][dual][uuid] */ 


EXTERN_C const IID IID_Errors;

#if defined(__cplusplus) && !defined(CINTERFACE)
    
    MIDL_INTERFACE("00000501-0000-0010-8000-00AA006D2EA4")
    Errors : public _Collection
    {
    public:
        virtual /* [propget][id] */ HRESULT __stdcall get_Item( 
            /* [in] */ VARIANT Index,
            /* [retval][out] */ Error **ppvObject) = 0;
        
        virtual /* [id] */ HRESULT __stdcall Clear( void) = 0;
        
    };
    
#else 	/* C style interface */

    typedef struct ErrorsVtbl
    {
        BEGIN_INTERFACE
        
        HRESULT ( STDMETHODCALLTYPE *QueryInterface )( 
            Errors * This,
            /* [in] */ REFIID riid,
            /* [annotation][iid_is][out] */ 
            __RPC__deref_out  void **ppvObject);
        
        ULONG ( STDMETHODCALLTYPE *AddRef )( 
            Errors * This);
        
        ULONG ( STDMETHODCALLTYPE *Release )( 
            Errors * This);
        
        HRESULT ( STDMETHODCALLTYPE *GetTypeInfoCount )( 
            Errors * This,
            /* [out] */ UINT *pctinfo);
        
        HRESULT ( STDMETHODCALLTYPE *GetTypeInfo )( 
            Errors * This,
            /* [in] */ UINT iTInfo,
            /* [in] */ LCID lcid,
            /* [out] */ ITypeInfo **ppTInfo);
        
        HRESULT ( STDMETHODCALLTYPE *GetIDsOfNames )( 
            Errors * This,
            /* [in] */ REFIID riid,
            /* [size_is][in] */ LPOLESTR *rgszNames,
            /* [range][in] */ UINT cNames,
            /* [in] */ LCID lcid,
            /* [size_is][out] */ DISPID *rgDispId);
        
        /* [local] */ HRESULT ( STDMETHODCALLTYPE *Invoke )( 
            Errors * This,
            /* [in] */ DISPID dispIdMember,
            /* [in] */ REFIID riid,
            /* [in] */ LCID lcid,
            /* [in] */ WORD wFlags,
            /* [out][in] */ DISPPARAMS *pDispParams,
            /* [out] */ VARIANT *pVarResult,
            /* [out] */ EXCEPINFO *pExcepInfo,
            /* [out] */ UINT *puArgErr);
        
        /* [propget][id] */ HRESULT ( __stdcall *get_Count )( 
            Errors * This,
            /* [retval][out] */ long *c);
        
        /* [restricted][id] */ HRESULT ( __stdcall *_NewEnum )( 
            Errors * This,
            /* [retval][out] */ IUnknown **ppvObject);
        
        /* [id] */ HRESULT ( __stdcall *Refresh )( 
            Errors * This);
        
        /* [propget][id] */ HRESULT ( __stdcall *get_Item )( 
            Errors * This,
            /* [in] */ VARIANT Index,
            /* [retval][out] */ Error **ppvObject);
        
        /* [id] */ HRESULT ( __stdcall *Clear )( 
            Errors * This);
        
        END_INTERFACE
    } ErrorsVtbl;

    interface Errors
    {
        CONST_VTBL struct ErrorsVtbl *lpVtbl;
    };

    

#ifdef COBJMACROS


#define Errors_QueryInterface(This,riid,ppvObject)	\
    ( (This)->lpVtbl -> QueryInterface(This,riid,ppvObject) ) 

#define Errors_AddRef(This)	\
    ( (This)->lpVtbl -> AddRef(This) ) 

#define Errors_Release(This)	\
    ( (This)->lpVtbl -> Release(This) ) 


#define Errors_GetTypeInfoCount(This,pctinfo)	\
    ( (This)->lpVtbl -> GetTypeInfoCount(This,pctinfo) ) 

#define Errors_GetTypeInfo(This,iTInfo,lcid,ppTInfo)	\
    ( (This)->lpVtbl -> GetTypeInfo(This,iTInfo,lcid,ppTInfo) ) 

#define Errors_GetIDsOfNames(This,riid,rgszNames,cNames,lcid,rgDispId)	\
    ( (This)->lpVtbl -> GetIDsOfNames(This,riid,rgszNames,cNames,lcid,rgDispId) ) 

#define Errors_Invoke(This,dispIdMember,riid,lcid,wFlags,pDispParams,pVarResult,pExcepInfo,puArgErr)	\
    ( (This)->lpVtbl -> Invoke(This,dispIdMember,riid,lcid,wFlags,pDispParams,pVarResult,pExcepInfo,puArgErr) ) 


#define Errors_get_Count(This,c)	\
    ( (This)->lpVtbl -> get_Count(This,c) ) 

#define Errors__NewEnum(This,ppvObject)	\
    ( (This)->lpVtbl -> _NewEnum(This,ppvObject) ) 

#define Errors_Refresh(This)	\
    ( (This)->lpVtbl -> Refresh(This) ) 


#define Errors_get_Item(This,Index,ppvObject)	\
    ( (This)->lpVtbl -> get_Item(This,Index,ppvObject) ) 

#define Errors_Clear(This)	\
    ( (This)->lpVtbl -> Clear(This) ) 

#endif /* COBJMACROS */


#endif 	/* C style interface */




#endif 	/* __Errors_INTERFACE_DEFINED__ */


#ifndef __Command15_INTERFACE_DEFINED__
#define __Command15_INTERFACE_DEFINED__

/* interface Command15 */
/* [object][oleautomation][nonextensible][dual][uuid] */ 


EXTERN_C const IID IID_Command15;

#if defined(__cplusplus) && !defined(CINTERFACE)
    
    MIDL_INTERFACE("00000508-0000-0010-8000-00AA006D2EA4")
    Command15 : public _ADO
    {
    public:
        virtual /* [propget][id] */ HRESULT __stdcall get_ActiveConnection( 
            /* [retval][out] */ _Connection **ppvObject) = 0;
        
        virtual /* [propputref][id] */ HRESULT __stdcall putref_ActiveConnection( 
            /* [in] */ _Connection *ppvObject) = 0;
        
        virtual /* [propput][id] */ HRESULT __stdcall put_ActiveConnection( 
            /* [in] */ VARIANT ppvObject) = 0;
        
        virtual /* [propget][id] */ HRESULT __stdcall get_CommandText( 
            /* [retval][out] */ BSTR *pbstr) = 0;
        
        virtual /* [propput][id] */ HRESULT __stdcall put_CommandText( 
            /* [in] */ BSTR pbstr) = 0;
        
        virtual /* [propget][id] */ HRESULT __stdcall get_CommandTimeout( 
            /* [retval][out] */ long *pl) = 0;
        
        virtual /* [propput][id] */ HRESULT __stdcall put_CommandTimeout( 
            /* [in] */ long pl) = 0;
        
        virtual /* [propget][id] */ HRESULT __stdcall get_Prepared( 
            /* [retval][out] */ VARIANT_BOOL *pfPrepared) = 0;
        
        virtual /* [propput][id] */ HRESULT __stdcall put_Prepared( 
            /* [in] */ VARIANT_BOOL pfPrepared) = 0;
        
        virtual /* [id] */ HRESULT __stdcall Execute( 
            /* [optional][out] */ VARIANT *RecordsAffected,
            /* [optional][in] */ VARIANT *Parameters,
            /* [defaultvalue][in] */ long Options,
            /* [retval][out] */ _Recordset **ppiRs) = 0;
        
        virtual /* [id] */ HRESULT __stdcall CreateParameter( 
            /* [defaultvalue][in] */ BSTR Name,
            /* [defaultvalue][in] */ DataTypeEnum Type,
            /* [defaultvalue][in] */ ParameterDirectionEnum Direction,
            /* [defaultvalue][in] */ ADO_LONGPTR Size,
            /* [optional][in] */ VARIANT Value,
            /* [retval][out] */ _Parameter **ppiprm) = 0;
        
        virtual /* [propget][id] */ HRESULT __stdcall get_Parameters( 
            /* [retval][out] */ Parameters **ppvObject) = 0;
        
        virtual /* [propput][id] */ HRESULT __stdcall put_CommandType( 
            /* [in] */ CommandTypeEnum plCmdType) = 0;
        
        virtual /* [propget][id] */ HRESULT __stdcall get_CommandType( 
            /* [retval][out] */ CommandTypeEnum *plCmdType) = 0;
        
        virtual /* [propget][id] */ HRESULT __stdcall get_Name( 
            /* [retval][out] */ BSTR *pbstrName) = 0;
        
        virtual /* [propput][id] */ HRESULT __stdcall put_Name( 
            /* [in] */ BSTR pbstrName) = 0;
        
    };
    
#else 	/* C style interface */

    typedef struct Command15Vtbl
    {
        BEGIN_INTERFACE
        
        HRESULT ( STDMETHODCALLTYPE *QueryInterface )( 
            Command15 * This,
            /* [in] */ REFIID riid,
            /* [annotation][iid_is][out] */ 
            __RPC__deref_out  void **ppvObject);
        
        ULONG ( STDMETHODCALLTYPE *AddRef )( 
            Command15 * This);
        
        ULONG ( STDMETHODCALLTYPE *Release )( 
            Command15 * This);
        
        HRESULT ( STDMETHODCALLTYPE *GetTypeInfoCount )( 
            Command15 * This,
            /* [out] */ UINT *pctinfo);
        
        HRESULT ( STDMETHODCALLTYPE *GetTypeInfo )( 
            Command15 * This,
            /* [in] */ UINT iTInfo,
            /* [in] */ LCID lcid,
            /* [out] */ ITypeInfo **ppTInfo);
        
        HRESULT ( STDMETHODCALLTYPE *GetIDsOfNames )( 
            Command15 * This,
            /* [in] */ REFIID riid,
            /* [size_is][in] */ LPOLESTR *rgszNames,
            /* [range][in] */ UINT cNames,
            /* [in] */ LCID lcid,
            /* [size_is][out] */ DISPID *rgDispId);
        
        /* [local] */ HRESULT ( STDMETHODCALLTYPE *Invoke )( 
            Command15 * This,
            /* [in] */ DISPID dispIdMember,
            /* [in] */ REFIID riid,
            /* [in] */ LCID lcid,
            /* [in] */ WORD wFlags,
            /* [out][in] */ DISPPARAMS *pDispParams,
            /* [out] */ VARIANT *pVarResult,
            /* [out] */ EXCEPINFO *pExcepInfo,
            /* [out] */ UINT *puArgErr);
        
        /* [propget][id] */ HRESULT ( __stdcall *get_Properties )( 
            Command15 * This,
            /* [retval][out] */ Properties **ppvObject);
        
        /* [propget][id] */ HRESULT ( __stdcall *get_ActiveConnection )( 
            Command15 * This,
            /* [retval][out] */ _Connection **ppvObject);
        
        /* [propputref][id] */ HRESULT ( __stdcall *putref_ActiveConnection )( 
            Command15 * This,
            /* [in] */ _Connection *ppvObject);
        
        /* [propput][id] */ HRESULT ( __stdcall *put_ActiveConnection )( 
            Command15 * This,
            /* [in] */ VARIANT ppvObject);
        
        /* [propget][id] */ HRESULT ( __stdcall *get_CommandText )( 
            Command15 * This,
            /* [retval][out] */ BSTR *pbstr);
        
        /* [propput][id] */ HRESULT ( __stdcall *put_CommandText )( 
            Command15 * This,
            /* [in] */ BSTR pbstr);
        
        /* [propget][id] */ HRESULT ( __stdcall *get_CommandTimeout )( 
            Command15 * This,
            /* [retval][out] */ long *pl);
        
        /* [propput][id] */ HRESULT ( __stdcall *put_CommandTimeout )( 
            Command15 * This,
            /* [in] */ long pl);
        
        /* [propget][id] */ HRESULT ( __stdcall *get_Prepared )( 
            Command15 * This,
            /* [retval][out] */ VARIANT_BOOL *pfPrepared);
        
        /* [propput][id] */ HRESULT ( __stdcall *put_Prepared )( 
            Command15 * This,
            /* [in] */ VARIANT_BOOL pfPrepared);
        
        /* [id] */ HRESULT ( __stdcall *Execute )( 
            Command15 * This,
            /* [optional][out] */ VARIANT *RecordsAffected,
            /* [optional][in] */ VARIANT *Parameters,
            /* [defaultvalue][in] */ long Options,
            /* [retval][out] */ _Recordset **ppiRs);
        
        /* [id] */ HRESULT ( __stdcall *CreateParameter )( 
            Command15 * This,
            /* [defaultvalue][in] */ BSTR Name,
            /* [defaultvalue][in] */ DataTypeEnum Type,
            /* [defaultvalue][in] */ ParameterDirectionEnum Direction,
            /* [defaultvalue][in] */ ADO_LONGPTR Size,
            /* [optional][in] */ VARIANT Value,
            /* [retval][out] */ _Parameter **ppiprm);
        
        /* [propget][id] */ HRESULT ( __stdcall *get_Parameters )( 
            Command15 * This,
            /* [retval][out] */ Parameters **ppvObject);
        
        /* [propput][id] */ HRESULT ( __stdcall *put_CommandType )( 
            Command15 * This,
            /* [in] */ CommandTypeEnum plCmdType);
        
        /* [propget][id] */ HRESULT ( __stdcall *get_CommandType )( 
            Command15 * This,
            /* [retval][out] */ CommandTypeEnum *plCmdType);
        
        /* [propget][id] */ HRESULT ( __stdcall *get_Name )( 
            Command15 * This,
            /* [retval][out] */ BSTR *pbstrName);
        
        /* [propput][id] */ HRESULT ( __stdcall *put_Name )( 
            Command15 * This,
            /* [in] */ BSTR pbstrName);
        
        END_INTERFACE
    } Command15Vtbl;

    interface Command15
    {
        CONST_VTBL struct Command15Vtbl *lpVtbl;
    };

    

#ifdef COBJMACROS


#define Command15_QueryInterface(This,riid,ppvObject)	\
    ( (This)->lpVtbl -> QueryInterface(This,riid,ppvObject) ) 

#define Command15_AddRef(This)	\
    ( (This)->lpVtbl -> AddRef(This) ) 

#define Command15_Release(This)	\
    ( (This)->lpVtbl -> Release(This) ) 


#define Command15_GetTypeInfoCount(This,pctinfo)	\
    ( (This)->lpVtbl -> GetTypeInfoCount(This,pctinfo) ) 

#define Command15_GetTypeInfo(This,iTInfo,lcid,ppTInfo)	\
    ( (This)->lpVtbl -> GetTypeInfo(This,iTInfo,lcid,ppTInfo) ) 

#define Command15_GetIDsOfNames(This,riid,rgszNames,cNames,lcid,rgDispId)	\
    ( (This)->lpVtbl -> GetIDsOfNames(This,riid,rgszNames,cNames,lcid,rgDispId) ) 

#define Command15_Invoke(This,dispIdMember,riid,lcid,wFlags,pDispParams,pVarResult,pExcepInfo,puArgErr)	\
    ( (This)->lpVtbl -> Invoke(This,dispIdMember,riid,lcid,wFlags,pDispParams,pVarResult,pExcepInfo,puArgErr) ) 


#define Command15_get_Properties(This,ppvObject)	\
    ( (This)->lpVtbl -> get_Properties(This,ppvObject) ) 


#define Command15_get_ActiveConnection(This,ppvObject)	\
    ( (This)->lpVtbl -> get_ActiveConnection(This,ppvObject) ) 

#define Command15_putref_ActiveConnection(This,ppvObject)	\
    ( (This)->lpVtbl -> putref_ActiveConnection(This,ppvObject) ) 

#define Command15_put_ActiveConnection(This,ppvObject)	\
    ( (This)->lpVtbl -> put_ActiveConnection(This,ppvObject) ) 

#define Command15_get_CommandText(This,pbstr)	\
    ( (This)->lpVtbl -> get_CommandText(This,pbstr) ) 

#define Command15_put_CommandText(This,pbstr)	\
    ( (This)->lpVtbl -> put_CommandText(This,pbstr) ) 

#define Command15_get_CommandTimeout(This,pl)	\
    ( (This)->lpVtbl -> get_CommandTimeout(This,pl) ) 

#define Command15_put_CommandTimeout(This,pl)	\
    ( (This)->lpVtbl -> put_CommandTimeout(This,pl) ) 

#define Command15_get_Prepared(This,pfPrepared)	\
    ( (This)->lpVtbl -> get_Prepared(This,pfPrepared) ) 

#define Command15_put_Prepared(This,pfPrepared)	\
    ( (This)->lpVtbl -> put_Prepared(This,pfPrepared) ) 

#define Command15_Execute(This,RecordsAffected,Parameters,Options,ppiRs)	\
    ( (This)->lpVtbl -> Execute(This,RecordsAffected,Parameters,Options,ppiRs) ) 

#define Command15_CreateParameter(This,Name,Type,Direction,Size,Value,ppiprm)	\
    ( (This)->lpVtbl -> CreateParameter(This,Name,Type,Direction,Size,Value,ppiprm) ) 

#define Command15_get_Parameters(This,ppvObject)	\
    ( (This)->lpVtbl -> get_Parameters(This,ppvObject) ) 

#define Command15_put_CommandType(This,plCmdType)	\
    ( (This)->lpVtbl -> put_CommandType(This,plCmdType) ) 

#define Command15_get_CommandType(This,plCmdType)	\
    ( (This)->lpVtbl -> get_CommandType(This,plCmdType) ) 

#define Command15_get_Name(This,pbstrName)	\
    ( (This)->lpVtbl -> get_Name(This,pbstrName) ) 

#define Command15_put_Name(This,pbstrName)	\
    ( (This)->lpVtbl -> put_Name(This,pbstrName) ) 

#endif /* COBJMACROS */


#endif 	/* C style interface */




#endif 	/* __Command15_INTERFACE_DEFINED__ */


#ifndef __Command25_INTERFACE_DEFINED__
#define __Command25_INTERFACE_DEFINED__

/* interface Command25 */
/* [object][oleautomation][nonextensible][dual][uuid] */ 


EXTERN_C const IID IID_Command25;

#if defined(__cplusplus) && !defined(CINTERFACE)
    
    MIDL_INTERFACE("0000054E-0000-0010-8000-00AA006D2EA4")
    Command25 : public Command15
    {
    public:
        virtual /* [propget][id] */ HRESULT __stdcall get_State( 
            /* [retval][out] */ long *plObjState) = 0;
        
        virtual /* [id] */ HRESULT __stdcall Cancel( void) = 0;
        
    };
    
#else 	/* C style interface */

    typedef struct Command25Vtbl
    {
        BEGIN_INTERFACE
        
        HRESULT ( STDMETHODCALLTYPE *QueryInterface )( 
            Command25 * This,
            /* [in] */ REFIID riid,
            /* [annotation][iid_is][out] */ 
            __RPC__deref_out  void **ppvObject);
        
        ULONG ( STDMETHODCALLTYPE *AddRef )( 
            Command25 * This);
        
        ULONG ( STDMETHODCALLTYPE *Release )( 
            Command25 * This);
        
        HRESULT ( STDMETHODCALLTYPE *GetTypeInfoCount )( 
            Command25 * This,
            /* [out] */ UINT *pctinfo);
        
        HRESULT ( STDMETHODCALLTYPE *GetTypeInfo )( 
            Command25 * This,
            /* [in] */ UINT iTInfo,
            /* [in] */ LCID lcid,
            /* [out] */ ITypeInfo **ppTInfo);
        
        HRESULT ( STDMETHODCALLTYPE *GetIDsOfNames )( 
            Command25 * This,
            /* [in] */ REFIID riid,
            /* [size_is][in] */ LPOLESTR *rgszNames,
            /* [range][in] */ UINT cNames,
            /* [in] */ LCID lcid,
            /* [size_is][out] */ DISPID *rgDispId);
        
        /* [local] */ HRESULT ( STDMETHODCALLTYPE *Invoke )( 
            Command25 * This,
            /* [in] */ DISPID dispIdMember,
            /* [in] */ REFIID riid,
            /* [in] */ LCID lcid,
            /* [in] */ WORD wFlags,
            /* [out][in] */ DISPPARAMS *pDispParams,
            /* [out] */ VARIANT *pVarResult,
            /* [out] */ EXCEPINFO *pExcepInfo,
            /* [out] */ UINT *puArgErr);
        
        /* [propget][id] */ HRESULT ( __stdcall *get_Properties )( 
            Command25 * This,
            /* [retval][out] */ Properties **ppvObject);
        
        /* [propget][id] */ HRESULT ( __stdcall *get_ActiveConnection )( 
            Command25 * This,
            /* [retval][out] */ _Connection **ppvObject);
        
        /* [propputref][id] */ HRESULT ( __stdcall *putref_ActiveConnection )( 
            Command25 * This,
            /* [in] */ _Connection *ppvObject);
        
        /* [propput][id] */ HRESULT ( __stdcall *put_ActiveConnection )( 
            Command25 * This,
            /* [in] */ VARIANT ppvObject);
        
        /* [propget][id] */ HRESULT ( __stdcall *get_CommandText )( 
            Command25 * This,
            /* [retval][out] */ BSTR *pbstr);
        
        /* [propput][id] */ HRESULT ( __stdcall *put_CommandText )( 
            Command25 * This,
            /* [in] */ BSTR pbstr);
        
        /* [propget][id] */ HRESULT ( __stdcall *get_CommandTimeout )( 
            Command25 * This,
            /* [retval][out] */ long *pl);
        
        /* [propput][id] */ HRESULT ( __stdcall *put_CommandTimeout )( 
            Command25 * This,
            /* [in] */ long pl);
        
        /* [propget][id] */ HRESULT ( __stdcall *get_Prepared )( 
            Command25 * This,
            /* [retval][out] */ VARIANT_BOOL *pfPrepared);
        
        /* [propput][id] */ HRESULT ( __stdcall *put_Prepared )( 
            Command25 * This,
            /* [in] */ VARIANT_BOOL pfPrepared);
        
        /* [id] */ HRESULT ( __stdcall *Execute )( 
            Command25 * This,
            /* [optional][out] */ VARIANT *RecordsAffected,
            /* [optional][in] */ VARIANT *Parameters,
            /* [defaultvalue][in] */ long Options,
            /* [retval][out] */ _Recordset **ppiRs);
        
        /* [id] */ HRESULT ( __stdcall *CreateParameter )( 
            Command25 * This,
            /* [defaultvalue][in] */ BSTR Name,
            /* [defaultvalue][in] */ DataTypeEnum Type,
            /* [defaultvalue][in] */ ParameterDirectionEnum Direction,
            /* [defaultvalue][in] */ ADO_LONGPTR Size,
            /* [optional][in] */ VARIANT Value,
            /* [retval][out] */ _Parameter **ppiprm);
        
        /* [propget][id] */ HRESULT ( __stdcall *get_Parameters )( 
            Command25 * This,
            /* [retval][out] */ Parameters **ppvObject);
        
        /* [propput][id] */ HRESULT ( __stdcall *put_CommandType )( 
            Command25 * This,
            /* [in] */ CommandTypeEnum plCmdType);
        
        /* [propget][id] */ HRESULT ( __stdcall *get_CommandType )( 
            Command25 * This,
            /* [retval][out] */ CommandTypeEnum *plCmdType);
        
        /* [propget][id] */ HRESULT ( __stdcall *get_Name )( 
            Command25 * This,
            /* [retval][out] */ BSTR *pbstrName);
        
        /* [propput][id] */ HRESULT ( __stdcall *put_Name )( 
            Command25 * This,
            /* [in] */ BSTR pbstrName);
        
        /* [propget][id] */ HRESULT ( __stdcall *get_State )( 
            Command25 * This,
            /* [retval][out] */ long *plObjState);
        
        /* [id] */ HRESULT ( __stdcall *Cancel )( 
            Command25 * This);
        
        END_INTERFACE
    } Command25Vtbl;

    interface Command25
    {
        CONST_VTBL struct Command25Vtbl *lpVtbl;
    };

    

#ifdef COBJMACROS


#define Command25_QueryInterface(This,riid,ppvObject)	\
    ( (This)->lpVtbl -> QueryInterface(This,riid,ppvObject) ) 

#define Command25_AddRef(This)	\
    ( (This)->lpVtbl -> AddRef(This) ) 

#define Command25_Release(This)	\
    ( (This)->lpVtbl -> Release(This) ) 


#define Command25_GetTypeInfoCount(This,pctinfo)	\
    ( (This)->lpVtbl -> GetTypeInfoCount(This,pctinfo) ) 

#define Command25_GetTypeInfo(This,iTInfo,lcid,ppTInfo)	\
    ( (This)->lpVtbl -> GetTypeInfo(This,iTInfo,lcid,ppTInfo) ) 

#define Command25_GetIDsOfNames(This,riid,rgszNames,cNames,lcid,rgDispId)	\
    ( (This)->lpVtbl -> GetIDsOfNames(This,riid,rgszNames,cNames,lcid,rgDispId) ) 

#define Command25_Invoke(This,dispIdMember,riid,lcid,wFlags,pDispParams,pVarResult,pExcepInfo,puArgErr)	\
    ( (This)->lpVtbl -> Invoke(This,dispIdMember,riid,lcid,wFlags,pDispParams,pVarResult,pExcepInfo,puArgErr) ) 


#define Command25_get_Properties(This,ppvObject)	\
    ( (This)->lpVtbl -> get_Properties(This,ppvObject) ) 


#define Command25_get_ActiveConnection(This,ppvObject)	\
    ( (This)->lpVtbl -> get_ActiveConnection(This,ppvObject) ) 

#define Command25_putref_ActiveConnection(This,ppvObject)	\
    ( (This)->lpVtbl -> putref_ActiveConnection(This,ppvObject) ) 

#define Command25_put_ActiveConnection(This,ppvObject)	\
    ( (This)->lpVtbl -> put_ActiveConnection(This,ppvObject) ) 

#define Command25_get_CommandText(This,pbstr)	\
    ( (This)->lpVtbl -> get_CommandText(This,pbstr) ) 

#define Command25_put_CommandText(This,pbstr)	\
    ( (This)->lpVtbl -> put_CommandText(This,pbstr) ) 

#define Command25_get_CommandTimeout(This,pl)	\
    ( (This)->lpVtbl -> get_CommandTimeout(This,pl) ) 

#define Command25_put_CommandTimeout(This,pl)	\
    ( (This)->lpVtbl -> put_CommandTimeout(This,pl) ) 

#define Command25_get_Prepared(This,pfPrepared)	\
    ( (This)->lpVtbl -> get_Prepared(This,pfPrepared) ) 

#define Command25_put_Prepared(This,pfPrepared)	\
    ( (This)->lpVtbl -> put_Prepared(This,pfPrepared) ) 

#define Command25_Execute(This,RecordsAffected,Parameters,Options,ppiRs)	\
    ( (This)->lpVtbl -> Execute(This,RecordsAffected,Parameters,Options,ppiRs) ) 

#define Command25_CreateParameter(This,Name,Type,Direction,Size,Value,ppiprm)	\
    ( (This)->lpVtbl -> CreateParameter(This,Name,Type,Direction,Size,Value,ppiprm) ) 

#define Command25_get_Parameters(This,ppvObject)	\
    ( (This)->lpVtbl -> get_Parameters(This,ppvObject) ) 

#define Command25_put_CommandType(This,plCmdType)	\
    ( (This)->lpVtbl -> put_CommandType(This,plCmdType) ) 

#define Command25_get_CommandType(This,plCmdType)	\
    ( (This)->lpVtbl -> get_CommandType(This,plCmdType) ) 

#define Command25_get_Name(This,pbstrName)	\
    ( (This)->lpVtbl -> get_Name(This,pbstrName) ) 

#define Command25_put_Name(This,pbstrName)	\
    ( (This)->lpVtbl -> put_Name(This,pbstrName) ) 


#define Command25_get_State(This,plObjState)	\
    ( (This)->lpVtbl -> get_State(This,plObjState) ) 

#define Command25_Cancel(This)	\
    ( (This)->lpVtbl -> Cancel(This) ) 

#endif /* COBJMACROS */


#endif 	/* C style interface */




#endif 	/* __Command25_INTERFACE_DEFINED__ */


#ifndef ___Command_INTERFACE_DEFINED__
#define ___Command_INTERFACE_DEFINED__

/* interface _Command */
/* [object][oleautomation][nonextensible][dual][uuid] */ 


EXTERN_C const IID IID__Command;

#if defined(__cplusplus) && !defined(CINTERFACE)
    
    MIDL_INTERFACE("B08400BD-F9D1-4D02-B856-71D5DBA123E9")
    _Command : public Command25
    {
    public:
        virtual /* [propputref][id] */ HRESULT __stdcall putref_CommandStream( 
            /* [in] */ IUnknown *pStream) = 0;
        
        virtual /* [propget][id] */ HRESULT __stdcall get_CommandStream( 
            /* [retval][out] */ VARIANT *pvStream) = 0;
        
        virtual /* [propput][id] */ HRESULT __stdcall put_Dialect( 
            /* [in] */ BSTR bstrDialect) = 0;
        
        virtual /* [propget][id] */ HRESULT __stdcall get_Dialect( 
            /* [retval][out] */ BSTR *pbstrDialect) = 0;
        
        virtual /* [propput][id] */ HRESULT __stdcall put_NamedParameters( 
            /* [in] */ VARIANT_BOOL fNamedParameters) = 0;
        
        virtual /* [propget][id] */ HRESULT __stdcall get_NamedParameters( 
            /* [retval][out] */ VARIANT_BOOL *pfNamedParameters) = 0;
        
    };
    
#else 	/* C style interface */

    typedef struct _CommandVtbl
    {
        BEGIN_INTERFACE
        
        HRESULT ( STDMETHODCALLTYPE *QueryInterface )( 
            _Command * This,
            /* [in] */ REFIID riid,
            /* [annotation][iid_is][out] */ 
            __RPC__deref_out  void **ppvObject);
        
        ULONG ( STDMETHODCALLTYPE *AddRef )( 
            _Command * This);
        
        ULONG ( STDMETHODCALLTYPE *Release )( 
            _Command * This);
        
        HRESULT ( STDMETHODCALLTYPE *GetTypeInfoCount )( 
            _Command * This,
            /* [out] */ UINT *pctinfo);
        
        HRESULT ( STDMETHODCALLTYPE *GetTypeInfo )( 
            _Command * This,
            /* [in] */ UINT iTInfo,
            /* [in] */ LCID lcid,
            /* [out] */ ITypeInfo **ppTInfo);
        
        HRESULT ( STDMETHODCALLTYPE *GetIDsOfNames )( 
            _Command * This,
            /* [in] */ REFIID riid,
            /* [size_is][in] */ LPOLESTR *rgszNames,
            /* [range][in] */ UINT cNames,
            /* [in] */ LCID lcid,
            /* [size_is][out] */ DISPID *rgDispId);
        
        /* [local] */ HRESULT ( STDMETHODCALLTYPE *Invoke )( 
            _Command * This,
            /* [in] */ DISPID dispIdMember,
            /* [in] */ REFIID riid,
            /* [in] */ LCID lcid,
            /* [in] */ WORD wFlags,
            /* [out][in] */ DISPPARAMS *pDispParams,
            /* [out] */ VARIANT *pVarResult,
            /* [out] */ EXCEPINFO *pExcepInfo,
            /* [out] */ UINT *puArgErr);
        
        /* [propget][id] */ HRESULT ( __stdcall *get_Properties )( 
            _Command * This,
            /* [retval][out] */ Properties **ppvObject);
        
        /* [propget][id] */ HRESULT ( __stdcall *get_ActiveConnection )( 
            _Command * This,
            /* [retval][out] */ _Connection **ppvObject);
        
        /* [propputref][id] */ HRESULT ( __stdcall *putref_ActiveConnection )( 
            _Command * This,
            /* [in] */ _Connection *ppvObject);
        
        /* [propput][id] */ HRESULT ( __stdcall *put_ActiveConnection )( 
            _Command * This,
            /* [in] */ VARIANT ppvObject);
        
        /* [propget][id] */ HRESULT ( __stdcall *get_CommandText )( 
            _Command * This,
            /* [retval][out] */ BSTR *pbstr);
        
        /* [propput][id] */ HRESULT ( __stdcall *put_CommandText )( 
            _Command * This,
            /* [in] */ BSTR pbstr);
        
        /* [propget][id] */ HRESULT ( __stdcall *get_CommandTimeout )( 
            _Command * This,
            /* [retval][out] */ long *pl);
        
        /* [propput][id] */ HRESULT ( __stdcall *put_CommandTimeout )( 
            _Command * This,
            /* [in] */ long pl);
        
        /* [propget][id] */ HRESULT ( __stdcall *get_Prepared )( 
            _Command * This,
            /* [retval][out] */ VARIANT_BOOL *pfPrepared);
        
        /* [propput][id] */ HRESULT ( __stdcall *put_Prepared )( 
            _Command * This,
            /* [in] */ VARIANT_BOOL pfPrepared);
        
        /* [id] */ HRESULT ( __stdcall *Execute )( 
            _Command * This,
            /* [optional][out] */ VARIANT *RecordsAffected,
            /* [optional][in] */ VARIANT *Parameters,
            /* [defaultvalue][in] */ long Options,
            /* [retval][out] */ _Recordset **ppiRs);
        
        /* [id] */ HRESULT ( __stdcall *CreateParameter )( 
            _Command * This,
            /* [defaultvalue][in] */ BSTR Name,
            /* [defaultvalue][in] */ DataTypeEnum Type,
            /* [defaultvalue][in] */ ParameterDirectionEnum Direction,
            /* [defaultvalue][in] */ ADO_LONGPTR Size,
            /* [optional][in] */ VARIANT Value,
            /* [retval][out] */ _Parameter **ppiprm);
        
        /* [propget][id] */ HRESULT ( __stdcall *get_Parameters )( 
            _Command * This,
            /* [retval][out] */ Parameters **ppvObject);
        
        /* [propput][id] */ HRESULT ( __stdcall *put_CommandType )( 
            _Command * This,
            /* [in] */ CommandTypeEnum plCmdType);
        
        /* [propget][id] */ HRESULT ( __stdcall *get_CommandType )( 
            _Command * This,
            /* [retval][out] */ CommandTypeEnum *plCmdType);
        
        /* [propget][id] */ HRESULT ( __stdcall *get_Name )( 
            _Command * This,
            /* [retval][out] */ BSTR *pbstrName);
        
        /* [propput][id] */ HRESULT ( __stdcall *put_Name )( 
            _Command * This,
            /* [in] */ BSTR pbstrName);
        
        /* [propget][id] */ HRESULT ( __stdcall *get_State )( 
            _Command * This,
            /* [retval][out] */ long *plObjState);
        
        /* [id] */ HRESULT ( __stdcall *Cancel )( 
            _Command * This);
        
        /* [propputref][id] */ HRESULT ( __stdcall *putref_CommandStream )( 
            _Command * This,
            /* [in] */ IUnknown *pStream);
        
        /* [propget][id] */ HRESULT ( __stdcall *get_CommandStream )( 
            _Command * This,
            /* [retval][out] */ VARIANT *pvStream);
        
        /* [propput][id] */ HRESULT ( __stdcall *put_Dialect )( 
            _Command * This,
            /* [in] */ BSTR bstrDialect);
        
        /* [propget][id] */ HRESULT ( __stdcall *get_Dialect )( 
            _Command * This,
            /* [retval][out] */ BSTR *pbstrDialect);
        
        /* [propput][id] */ HRESULT ( __stdcall *put_NamedParameters )( 
            _Command * This,
            /* [in] */ VARIANT_BOOL fNamedParameters);
        
        /* [propget][id] */ HRESULT ( __stdcall *get_NamedParameters )( 
            _Command * This,
            /* [retval][out] */ VARIANT_BOOL *pfNamedParameters);
        
        END_INTERFACE
    } _CommandVtbl;

    interface _Command
    {
        CONST_VTBL struct _CommandVtbl *lpVtbl;
    };

    

#ifdef COBJMACROS


#define _Command_QueryInterface(This,riid,ppvObject)	\
    ( (This)->lpVtbl -> QueryInterface(This,riid,ppvObject) ) 

#define _Command_AddRef(This)	\
    ( (This)->lpVtbl -> AddRef(This) ) 

#define _Command_Release(This)	\
    ( (This)->lpVtbl -> Release(This) ) 


#define _Command_GetTypeInfoCount(This,pctinfo)	\
    ( (This)->lpVtbl -> GetTypeInfoCount(This,pctinfo) ) 

#define _Command_GetTypeInfo(This,iTInfo,lcid,ppTInfo)	\
    ( (This)->lpVtbl -> GetTypeInfo(This,iTInfo,lcid,ppTInfo) ) 

#define _Command_GetIDsOfNames(This,riid,rgszNames,cNames,lcid,rgDispId)	\
    ( (This)->lpVtbl -> GetIDsOfNames(This,riid,rgszNames,cNames,lcid,rgDispId) ) 

#define _Command_Invoke(This,dispIdMember,riid,lcid,wFlags,pDispParams,pVarResult,pExcepInfo,puArgErr)	\
    ( (This)->lpVtbl -> Invoke(This,dispIdMember,riid,lcid,wFlags,pDispParams,pVarResult,pExcepInfo,puArgErr) ) 


#define _Command_get_Properties(This,ppvObject)	\
    ( (This)->lpVtbl -> get_Properties(This,ppvObject) ) 


#define _Command_get_ActiveConnection(This,ppvObject)	\
    ( (This)->lpVtbl -> get_ActiveConnection(This,ppvObject) ) 

#define _Command_putref_ActiveConnection(This,ppvObject)	\
    ( (This)->lpVtbl -> putref_ActiveConnection(This,ppvObject) ) 

#define _Command_put_ActiveConnection(This,ppvObject)	\
    ( (This)->lpVtbl -> put_ActiveConnection(This,ppvObject) ) 

#define _Command_get_CommandText(This,pbstr)	\
    ( (This)->lpVtbl -> get_CommandText(This,pbstr) ) 

#define _Command_put_CommandText(This,pbstr)	\
    ( (This)->lpVtbl -> put_CommandText(This,pbstr) ) 

#define _Command_get_CommandTimeout(This,pl)	\
    ( (This)->lpVtbl -> get_CommandTimeout(This,pl) ) 

#define _Command_put_CommandTimeout(This,pl)	\
    ( (This)->lpVtbl -> put_CommandTimeout(This,pl) ) 

#define _Command_get_Prepared(This,pfPrepared)	\
    ( (This)->lpVtbl -> get_Prepared(This,pfPrepared) ) 

#define _Command_put_Prepared(This,pfPrepared)	\
    ( (This)->lpVtbl -> put_Prepared(This,pfPrepared) ) 

#define _Command_Execute(This,RecordsAffected,Parameters,Options,ppiRs)	\
    ( (This)->lpVtbl -> Execute(This,RecordsAffected,Parameters,Options,ppiRs) ) 

#define _Command_CreateParameter(This,Name,Type,Direction,Size,Value,ppiprm)	\
    ( (This)->lpVtbl -> CreateParameter(This,Name,Type,Direction,Size,Value,ppiprm) ) 

#define _Command_get_Parameters(This,ppvObject)	\
    ( (This)->lpVtbl -> get_Parameters(This,ppvObject) ) 

#define _Command_put_CommandType(This,plCmdType)	\
    ( (This)->lpVtbl -> put_CommandType(This,plCmdType) ) 

#define _Command_get_CommandType(This,plCmdType)	\
    ( (This)->lpVtbl -> get_CommandType(This,plCmdType) ) 

#define _Command_get_Name(This,pbstrName)	\
    ( (This)->lpVtbl -> get_Name(This,pbstrName) ) 

#define _Command_put_Name(This,pbstrName)	\
    ( (This)->lpVtbl -> put_Name(This,pbstrName) ) 


#define _Command_get_State(This,plObjState)	\
    ( (This)->lpVtbl -> get_State(This,plObjState) ) 

#define _Command_Cancel(This)	\
    ( (This)->lpVtbl -> Cancel(This) ) 


#define _Command_putref_CommandStream(This,pStream)	\
    ( (This)->lpVtbl -> putref_CommandStream(This,pStream) ) 

#define _Command_get_CommandStream(This,pvStream)	\
    ( (This)->lpVtbl -> get_CommandStream(This,pvStream) ) 

#define _Command_put_Dialect(This,bstrDialect)	\
    ( (This)->lpVtbl -> put_Dialect(This,bstrDialect) ) 

#define _Command_get_Dialect(This,pbstrDialect)	\
    ( (This)->lpVtbl -> get_Dialect(This,pbstrDialect) ) 

#define _Command_put_NamedParameters(This,fNamedParameters)	\
    ( (This)->lpVtbl -> put_NamedParameters(This,fNamedParameters) ) 

#define _Command_get_NamedParameters(This,pfNamedParameters)	\
    ( (This)->lpVtbl -> get_NamedParameters(This,pfNamedParameters) ) 

#endif /* COBJMACROS */


#endif 	/* C style interface */




#endif 	/* ___Command_INTERFACE_DEFINED__ */


#ifndef __Connection15_INTERFACE_DEFINED__
#define __Connection15_INTERFACE_DEFINED__

/* interface Connection15 */
/* [object][oleautomation][dual][uuid] */ 


EXTERN_C const IID IID_Connection15;

#if defined(__cplusplus) && !defined(CINTERFACE)
    
    MIDL_INTERFACE("00000515-0000-0010-8000-00AA006D2EA4")
    Connection15 : public _ADO
    {
    public:
        virtual /* [propget][id] */ HRESULT __stdcall get_ConnectionString( 
            /* [retval][out] */ BSTR *pbstr) = 0;
        
        virtual /* [propput][id] */ HRESULT __stdcall put_ConnectionString( 
            /* [in] */ BSTR pbstr) = 0;
        
        virtual /* [propget][id] */ HRESULT __stdcall get_CommandTimeout( 
            /* [retval][out] */ long *plTimeout) = 0;
        
        virtual /* [propput][id] */ HRESULT __stdcall put_CommandTimeout( 
            /* [in] */ long plTimeout) = 0;
        
        virtual /* [propget][id] */ HRESULT __stdcall get_ConnectionTimeout( 
            /* [retval][out] */ long *plTimeout) = 0;
        
        virtual /* [propput][id] */ HRESULT __stdcall put_ConnectionTimeout( 
            /* [in] */ long plTimeout) = 0;
        
        virtual /* [propget][id] */ HRESULT __stdcall get_Version( 
            /* [retval][out] */ BSTR *pbstr) = 0;
        
        virtual /* [id] */ HRESULT __stdcall Close( void) = 0;
        
        virtual /* [id] */ HRESULT __stdcall Execute( 
            /* [in] */ BSTR CommandText,
            /* [optional][out] */ VARIANT *RecordsAffected,
            /* [defaultvalue][in] */ long Options,
            /* [retval][out] */ _Recordset **ppiRset) = 0;
        
        virtual /* [id] */ HRESULT __stdcall BeginTrans( 
            /* [retval][out] */ long *TransactionLevel) = 0;
        
        virtual /* [id] */ HRESULT __stdcall CommitTrans( void) = 0;
        
        virtual /* [id] */ HRESULT __stdcall RollbackTrans( void) = 0;
        
        virtual /* [id] */ HRESULT __stdcall Open( 
            /* [defaultvalue][in] */ BSTR ConnectionString = L"",
            /* [defaultvalue][in] */ BSTR UserID = L"",
            /* [defaultvalue][in] */ BSTR Password = L"",
            /* [defaultvalue][in] */ long Options = -1) = 0;
        
        virtual /* [propget][id] */ HRESULT __stdcall get_Errors( 
            /* [retval][out] */ Errors **ppvObject) = 0;
        
        virtual /* [propget][id] */ HRESULT __stdcall get_DefaultDatabase( 
            /* [retval][out] */ BSTR *pbstr) = 0;
        
        virtual /* [propput][id] */ HRESULT __stdcall put_DefaultDatabase( 
            /* [in] */ BSTR pbstr) = 0;
        
        virtual /* [propget][id] */ HRESULT __stdcall get_IsolationLevel( 
            /* [retval][out] */ IsolationLevelEnum *Level) = 0;
        
        virtual /* [propput][id] */ HRESULT __stdcall put_IsolationLevel( 
            /* [in] */ IsolationLevelEnum Level) = 0;
        
        virtual /* [propget][id] */ HRESULT __stdcall get_Attributes( 
            /* [retval][out] */ long *plAttr) = 0;
        
        virtual /* [propput][id] */ HRESULT __stdcall put_Attributes( 
            /* [in] */ long plAttr) = 0;
        
        virtual /* [propget][id] */ HRESULT __stdcall get_CursorLocation( 
            /* [retval][out] */ CursorLocationEnum *plCursorLoc) = 0;
        
        virtual /* [propput][id] */ HRESULT __stdcall put_CursorLocation( 
            /* [in] */ CursorLocationEnum plCursorLoc) = 0;
        
        virtual /* [propget][id] */ HRESULT __stdcall get_Mode( 
            /* [retval][out] */ ConnectModeEnum *plMode) = 0;
        
        virtual /* [propput][id] */ HRESULT __stdcall put_Mode( 
            /* [in] */ ConnectModeEnum plMode) = 0;
        
        virtual /* [propget][id] */ HRESULT __stdcall get_Provider( 
            /* [retval][out] */ BSTR *pbstr) = 0;
        
        virtual /* [propput][id] */ HRESULT __stdcall put_Provider( 
            /* [in] */ BSTR pbstr) = 0;
        
        virtual /* [propget][id] */ HRESULT __stdcall get_State( 
            /* [retval][out] */ long *plObjState) = 0;
        
        virtual /* [id] */ HRESULT __stdcall OpenSchema( 
            /* [in] */ SchemaEnum Schema,
            /* [optional][in] */ VARIANT Restrictions,
            /* [optional][in] */ VARIANT SchemaID,
            /* [retval][out] */ _Recordset **pprset) = 0;
        
    };
    
#else 	/* C style interface */

    typedef struct Connection15Vtbl
    {
        BEGIN_INTERFACE
        
        HRESULT ( STDMETHODCALLTYPE *QueryInterface )( 
            Connection15 * This,
            /* [in] */ REFIID riid,
            /* [annotation][iid_is][out] */ 
            __RPC__deref_out  void **ppvObject);
        
        ULONG ( STDMETHODCALLTYPE *AddRef )( 
            Connection15 * This);
        
        ULONG ( STDMETHODCALLTYPE *Release )( 
            Connection15 * This);
        
        HRESULT ( STDMETHODCALLTYPE *GetTypeInfoCount )( 
            Connection15 * This,
            /* [out] */ UINT *pctinfo);
        
        HRESULT ( STDMETHODCALLTYPE *GetTypeInfo )( 
            Connection15 * This,
            /* [in] */ UINT iTInfo,
            /* [in] */ LCID lcid,
            /* [out] */ ITypeInfo **ppTInfo);
        
        HRESULT ( STDMETHODCALLTYPE *GetIDsOfNames )( 
            Connection15 * This,
            /* [in] */ REFIID riid,
            /* [size_is][in] */ LPOLESTR *rgszNames,
            /* [range][in] */ UINT cNames,
            /* [in] */ LCID lcid,
            /* [size_is][out] */ DISPID *rgDispId);
        
        /* [local] */ HRESULT ( STDMETHODCALLTYPE *Invoke )( 
            Connection15 * This,
            /* [in] */ DISPID dispIdMember,
            /* [in] */ REFIID riid,
            /* [in] */ LCID lcid,
            /* [in] */ WORD wFlags,
            /* [out][in] */ DISPPARAMS *pDispParams,
            /* [out] */ VARIANT *pVarResult,
            /* [out] */ EXCEPINFO *pExcepInfo,
            /* [out] */ UINT *puArgErr);
        
        /* [propget][id] */ HRESULT ( __stdcall *get_Properties )( 
            Connection15 * This,
            /* [retval][out] */ Properties **ppvObject);
        
        /* [propget][id] */ HRESULT ( __stdcall *get_ConnectionString )( 
            Connection15 * This,
            /* [retval][out] */ BSTR *pbstr);
        
        /* [propput][id] */ HRESULT ( __stdcall *put_ConnectionString )( 
            Connection15 * This,
            /* [in] */ BSTR pbstr);
        
        /* [propget][id] */ HRESULT ( __stdcall *get_CommandTimeout )( 
            Connection15 * This,
            /* [retval][out] */ long *plTimeout);
        
        /* [propput][id] */ HRESULT ( __stdcall *put_CommandTimeout )( 
            Connection15 * This,
            /* [in] */ long plTimeout);
        
        /* [propget][id] */ HRESULT ( __stdcall *get_ConnectionTimeout )( 
            Connection15 * This,
            /* [retval][out] */ long *plTimeout);
        
        /* [propput][id] */ HRESULT ( __stdcall *put_ConnectionTimeout )( 
            Connection15 * This,
            /* [in] */ long plTimeout);
        
        /* [propget][id] */ HRESULT ( __stdcall *get_Version )( 
            Connection15 * This,
            /* [retval][out] */ BSTR *pbstr);
        
        /* [id] */ HRESULT ( __stdcall *Close )( 
            Connection15 * This);
        
        /* [id] */ HRESULT ( __stdcall *Execute )( 
            Connection15 * This,
            /* [in] */ BSTR CommandText,
            /* [optional][out] */ VARIANT *RecordsAffected,
            /* [defaultvalue][in] */ long Options,
            /* [retval][out] */ _Recordset **ppiRset);
        
        /* [id] */ HRESULT ( __stdcall *BeginTrans )( 
            Connection15 * This,
            /* [retval][out] */ long *TransactionLevel);
        
        /* [id] */ HRESULT ( __stdcall *CommitTrans )( 
            Connection15 * This);
        
        /* [id] */ HRESULT ( __stdcall *RollbackTrans )( 
            Connection15 * This);
        
        /* [id] */ HRESULT ( __stdcall *Open )( 
            Connection15 * This,
            /* [defaultvalue][in] */ BSTR ConnectionString,
            /* [defaultvalue][in] */ BSTR UserID,
            /* [defaultvalue][in] */ BSTR Password,
            /* [defaultvalue][in] */ long Options);
        
        /* [propget][id] */ HRESULT ( __stdcall *get_Errors )( 
            Connection15 * This,
            /* [retval][out] */ Errors **ppvObject);
        
        /* [propget][id] */ HRESULT ( __stdcall *get_DefaultDatabase )( 
            Connection15 * This,
            /* [retval][out] */ BSTR *pbstr);
        
        /* [propput][id] */ HRESULT ( __stdcall *put_DefaultDatabase )( 
            Connection15 * This,
            /* [in] */ BSTR pbstr);
        
        /* [propget][id] */ HRESULT ( __stdcall *get_IsolationLevel )( 
            Connection15 * This,
            /* [retval][out] */ IsolationLevelEnum *Level);
        
        /* [propput][id] */ HRESULT ( __stdcall *put_IsolationLevel )( 
            Connection15 * This,
            /* [in] */ IsolationLevelEnum Level);
        
        /* [propget][id] */ HRESULT ( __stdcall *get_Attributes )( 
            Connection15 * This,
            /* [retval][out] */ long *plAttr);
        
        /* [propput][id] */ HRESULT ( __stdcall *put_Attributes )( 
            Connection15 * This,
            /* [in] */ long plAttr);
        
        /* [propget][id] */ HRESULT ( __stdcall *get_CursorLocation )( 
            Connection15 * This,
            /* [retval][out] */ CursorLocationEnum *plCursorLoc);
        
        /* [propput][id] */ HRESULT ( __stdcall *put_CursorLocation )( 
            Connection15 * This,
            /* [in] */ CursorLocationEnum plCursorLoc);
        
        /* [propget][id] */ HRESULT ( __stdcall *get_Mode )( 
            Connection15 * This,
            /* [retval][out] */ ConnectModeEnum *plMode);
        
        /* [propput][id] */ HRESULT ( __stdcall *put_Mode )( 
            Connection15 * This,
            /* [in] */ ConnectModeEnum plMode);
        
        /* [propget][id] */ HRESULT ( __stdcall *get_Provider )( 
            Connection15 * This,
            /* [retval][out] */ BSTR *pbstr);
        
        /* [propput][id] */ HRESULT ( __stdcall *put_Provider )( 
            Connection15 * This,
            /* [in] */ BSTR pbstr);
        
        /* [propget][id] */ HRESULT ( __stdcall *get_State )( 
            Connection15 * This,
            /* [retval][out] */ long *plObjState);
        
        /* [id] */ HRESULT ( __stdcall *OpenSchema )( 
            Connection15 * This,
            /* [in] */ SchemaEnum Schema,
            /* [optional][in] */ VARIANT Restrictions,
            /* [optional][in] */ VARIANT SchemaID,
            /* [retval][out] */ _Recordset **pprset);
        
        END_INTERFACE
    } Connection15Vtbl;

    interface Connection15
    {
        CONST_VTBL struct Connection15Vtbl *lpVtbl;
    };

    

#ifdef COBJMACROS


#define Connection15_QueryInterface(This,riid,ppvObject)	\
    ( (This)->lpVtbl -> QueryInterface(This,riid,ppvObject) ) 

#define Connection15_AddRef(This)	\
    ( (This)->lpVtbl -> AddRef(This) ) 

#define Connection15_Release(This)	\
    ( (This)->lpVtbl -> Release(This) ) 


#define Connection15_GetTypeInfoCount(This,pctinfo)	\
    ( (This)->lpVtbl -> GetTypeInfoCount(This,pctinfo) ) 

#define Connection15_GetTypeInfo(This,iTInfo,lcid,ppTInfo)	\
    ( (This)->lpVtbl -> GetTypeInfo(This,iTInfo,lcid,ppTInfo) ) 

#define Connection15_GetIDsOfNames(This,riid,rgszNames,cNames,lcid,rgDispId)	\
    ( (This)->lpVtbl -> GetIDsOfNames(This,riid,rgszNames,cNames,lcid,rgDispId) ) 

#define Connection15_Invoke(This,dispIdMember,riid,lcid,wFlags,pDispParams,pVarResult,pExcepInfo,puArgErr)	\
    ( (This)->lpVtbl -> Invoke(This,dispIdMember,riid,lcid,wFlags,pDispParams,pVarResult,pExcepInfo,puArgErr) ) 


#define Connection15_get_Properties(This,ppvObject)	\
    ( (This)->lpVtbl -> get_Properties(This,ppvObject) ) 


#define Connection15_get_ConnectionString(This,pbstr)	\
    ( (This)->lpVtbl -> get_ConnectionString(This,pbstr) ) 

#define Connection15_put_ConnectionString(This,pbstr)	\
    ( (This)->lpVtbl -> put_ConnectionString(This,pbstr) ) 

#define Connection15_get_CommandTimeout(This,plTimeout)	\
    ( (This)->lpVtbl -> get_CommandTimeout(This,plTimeout) ) 

#define Connection15_put_CommandTimeout(This,plTimeout)	\
    ( (This)->lpVtbl -> put_CommandTimeout(This,plTimeout) ) 

#define Connection15_get_ConnectionTimeout(This,plTimeout)	\
    ( (This)->lpVtbl -> get_ConnectionTimeout(This,plTimeout) ) 

#define Connection15_put_ConnectionTimeout(This,plTimeout)	\
    ( (This)->lpVtbl -> put_ConnectionTimeout(This,plTimeout) ) 

#define Connection15_get_Version(This,pbstr)	\
    ( (This)->lpVtbl -> get_Version(This,pbstr) ) 

#define Connection15_Close(This)	\
    ( (This)->lpVtbl -> Close(This) ) 

#define Connection15_Execute(This,CommandText,RecordsAffected,Options,ppiRset)	\
    ( (This)->lpVtbl -> Execute(This,CommandText,RecordsAffected,Options,ppiRset) ) 

#define Connection15_BeginTrans(This,TransactionLevel)	\
    ( (This)->lpVtbl -> BeginTrans(This,TransactionLevel) ) 

#define Connection15_CommitTrans(This)	\
    ( (This)->lpVtbl -> CommitTrans(This) ) 

#define Connection15_RollbackTrans(This)	\
    ( (This)->lpVtbl -> RollbackTrans(This) ) 

#define Connection15_Open(This,ConnectionString,UserID,Password,Options)	\
    ( (This)->lpVtbl -> Open(This,ConnectionString,UserID,Password,Options) ) 

#define Connection15_get_Errors(This,ppvObject)	\
    ( (This)->lpVtbl -> get_Errors(This,ppvObject) ) 

#define Connection15_get_DefaultDatabase(This,pbstr)	\
    ( (This)->lpVtbl -> get_DefaultDatabase(This,pbstr) ) 

#define Connection15_put_DefaultDatabase(This,pbstr)	\
    ( (This)->lpVtbl -> put_DefaultDatabase(This,pbstr) ) 

#define Connection15_get_IsolationLevel(This,Level)	\
    ( (This)->lpVtbl -> get_IsolationLevel(This,Level) ) 

#define Connection15_put_IsolationLevel(This,Level)	\
    ( (This)->lpVtbl -> put_IsolationLevel(This,Level) ) 

#define Connection15_get_Attributes(This,plAttr)	\
    ( (This)->lpVtbl -> get_Attributes(This,plAttr) ) 

#define Connection15_put_Attributes(This,plAttr)	\
    ( (This)->lpVtbl -> put_Attributes(This,plAttr) ) 

#define Connection15_get_CursorLocation(This,plCursorLoc)	\
    ( (This)->lpVtbl -> get_CursorLocation(This,plCursorLoc) ) 

#define Connection15_put_CursorLocation(This,plCursorLoc)	\
    ( (This)->lpVtbl -> put_CursorLocation(This,plCursorLoc) ) 

#define Connection15_get_Mode(This,plMode)	\
    ( (This)->lpVtbl -> get_Mode(This,plMode) ) 

#define Connection15_put_Mode(This,plMode)	\
    ( (This)->lpVtbl -> put_Mode(This,plMode) ) 

#define Connection15_get_Provider(This,pbstr)	\
    ( (This)->lpVtbl -> get_Provider(This,pbstr) ) 

#define Connection15_put_Provider(This,pbstr)	\
    ( (This)->lpVtbl -> put_Provider(This,pbstr) ) 

#define Connection15_get_State(This,plObjState)	\
    ( (This)->lpVtbl -> get_State(This,plObjState) ) 

#define Connection15_OpenSchema(This,Schema,Restrictions,SchemaID,pprset)	\
    ( (This)->lpVtbl -> OpenSchema(This,Schema,Restrictions,SchemaID,pprset) ) 

#endif /* COBJMACROS */


#endif 	/* C style interface */




#endif 	/* __Connection15_INTERFACE_DEFINED__ */


#ifndef ___Connection_INTERFACE_DEFINED__
#define ___Connection_INTERFACE_DEFINED__

/* interface _Connection */
/* [object][oleautomation][dual][uuid] */ 


EXTERN_C const IID IID__Connection;

#if defined(__cplusplus) && !defined(CINTERFACE)
    
    MIDL_INTERFACE("00000550-0000-0010-8000-00AA006D2EA4")
    _Connection : public Connection15
    {
    public:
        virtual /* [id] */ HRESULT __stdcall Cancel( void) = 0;
        
    };
    
#else 	/* C style interface */

    typedef struct _ConnectionVtbl
    {
        BEGIN_INTERFACE
        
        HRESULT ( STDMETHODCALLTYPE *QueryInterface )( 
            _Connection * This,
            /* [in] */ REFIID riid,
            /* [annotation][iid_is][out] */ 
            __RPC__deref_out  void **ppvObject);
        
        ULONG ( STDMETHODCALLTYPE *AddRef )( 
            _Connection * This);
        
        ULONG ( STDMETHODCALLTYPE *Release )( 
            _Connection * This);
        
        HRESULT ( STDMETHODCALLTYPE *GetTypeInfoCount )( 
            _Connection * This,
            /* [out] */ UINT *pctinfo);
        
        HRESULT ( STDMETHODCALLTYPE *GetTypeInfo )( 
            _Connection * This,
            /* [in] */ UINT iTInfo,
            /* [in] */ LCID lcid,
            /* [out] */ ITypeInfo **ppTInfo);
        
        HRESULT ( STDMETHODCALLTYPE *GetIDsOfNames )( 
            _Connection * This,
            /* [in] */ REFIID riid,
            /* [size_is][in] */ LPOLESTR *rgszNames,
            /* [range][in] */ UINT cNames,
            /* [in] */ LCID lcid,
            /* [size_is][out] */ DISPID *rgDispId);
        
        /* [local] */ HRESULT ( STDMETHODCALLTYPE *Invoke )( 
            _Connection * This,
            /* [in] */ DISPID dispIdMember,
            /* [in] */ REFIID riid,
            /* [in] */ LCID lcid,
            /* [in] */ WORD wFlags,
            /* [out][in] */ DISPPARAMS *pDispParams,
            /* [out] */ VARIANT *pVarResult,
            /* [out] */ EXCEPINFO *pExcepInfo,
            /* [out] */ UINT *puArgErr);
        
        /* [propget][id] */ HRESULT ( __stdcall *get_Properties )( 
            _Connection * This,
            /* [retval][out] */ Properties **ppvObject);
        
        /* [propget][id] */ HRESULT ( __stdcall *get_ConnectionString )( 
            _Connection * This,
            /* [retval][out] */ BSTR *pbstr);
        
        /* [propput][id] */ HRESULT ( __stdcall *put_ConnectionString )( 
            _Connection * This,
            /* [in] */ BSTR pbstr);
        
        /* [propget][id] */ HRESULT ( __stdcall *get_CommandTimeout )( 
            _Connection * This,
            /* [retval][out] */ long *plTimeout);
        
        /* [propput][id] */ HRESULT ( __stdcall *put_CommandTimeout )( 
            _Connection * This,
            /* [in] */ long plTimeout);
        
        /* [propget][id] */ HRESULT ( __stdcall *get_ConnectionTimeout )( 
            _Connection * This,
            /* [retval][out] */ long *plTimeout);
        
        /* [propput][id] */ HRESULT ( __stdcall *put_ConnectionTimeout )( 
            _Connection * This,
            /* [in] */ long plTimeout);
        
        /* [propget][id] */ HRESULT ( __stdcall *get_Version )( 
            _Connection * This,
            /* [retval][out] */ BSTR *pbstr);
        
        /* [id] */ HRESULT ( __stdcall *Close )( 
            _Connection * This);
        
        /* [id] */ HRESULT ( __stdcall *Execute )( 
            _Connection * This,
            /* [in] */ BSTR CommandText,
            /* [optional][out] */ VARIANT *RecordsAffected,
            /* [defaultvalue][in] */ long Options,
            /* [retval][out] */ _Recordset **ppiRset);
        
        /* [id] */ HRESULT ( __stdcall *BeginTrans )( 
            _Connection * This,
            /* [retval][out] */ long *TransactionLevel);
        
        /* [id] */ HRESULT ( __stdcall *CommitTrans )( 
            _Connection * This);
        
        /* [id] */ HRESULT ( __stdcall *RollbackTrans )( 
            _Connection * This);
        
        /* [id] */ HRESULT ( __stdcall *Open )( 
            _Connection * This,
            /* [defaultvalue][in] */ BSTR ConnectionString,
            /* [defaultvalue][in] */ BSTR UserID,
            /* [defaultvalue][in] */ BSTR Password,
            /* [defaultvalue][in] */ long Options);
        
        /* [propget][id] */ HRESULT ( __stdcall *get_Errors )( 
            _Connection * This,
            /* [retval][out] */ Errors **ppvObject);
        
        /* [propget][id] */ HRESULT ( __stdcall *get_DefaultDatabase )( 
            _Connection * This,
            /* [retval][out] */ BSTR *pbstr);
        
        /* [propput][id] */ HRESULT ( __stdcall *put_DefaultDatabase )( 
            _Connection * This,
            /* [in] */ BSTR pbstr);
        
        /* [propget][id] */ HRESULT ( __stdcall *get_IsolationLevel )( 
            _Connection * This,
            /* [retval][out] */ IsolationLevelEnum *Level);
        
        /* [propput][id] */ HRESULT ( __stdcall *put_IsolationLevel )( 
            _Connection * This,
            /* [in] */ IsolationLevelEnum Level);
        
        /* [propget][id] */ HRESULT ( __stdcall *get_Attributes )( 
            _Connection * This,
            /* [retval][out] */ long *plAttr);
        
        /* [propput][id] */ HRESULT ( __stdcall *put_Attributes )( 
            _Connection * This,
            /* [in] */ long plAttr);
        
        /* [propget][id] */ HRESULT ( __stdcall *get_CursorLocation )( 
            _Connection * This,
            /* [retval][out] */ CursorLocationEnum *plCursorLoc);
        
        /* [propput][id] */ HRESULT ( __stdcall *put_CursorLocation )( 
            _Connection * This,
            /* [in] */ CursorLocationEnum plCursorLoc);
        
        /* [propget][id] */ HRESULT ( __stdcall *get_Mode )( 
            _Connection * This,
            /* [retval][out] */ ConnectModeEnum *plMode);
        
        /* [propput][id] */ HRESULT ( __stdcall *put_Mode )( 
            _Connection * This,
            /* [in] */ ConnectModeEnum plMode);
        
        /* [propget][id] */ HRESULT ( __stdcall *get_Provider )( 
            _Connection * This,
            /* [retval][out] */ BSTR *pbstr);
        
        /* [propput][id] */ HRESULT ( __stdcall *put_Provider )( 
            _Connection * This,
            /* [in] */ BSTR pbstr);
        
        /* [propget][id] */ HRESULT ( __stdcall *get_State )( 
            _Connection * This,
            /* [retval][out] */ long *plObjState);
        
        /* [id] */ HRESULT ( __stdcall *OpenSchema )( 
            _Connection * This,
            /* [in] */ SchemaEnum Schema,
            /* [optional][in] */ VARIANT Restrictions,
            /* [optional][in] */ VARIANT SchemaID,
            /* [retval][out] */ _Recordset **pprset);
        
        /* [id] */ HRESULT ( __stdcall *Cancel )( 
            _Connection * This);
        
        END_INTERFACE
    } _ConnectionVtbl;

    interface _Connection
    {
        CONST_VTBL struct _ConnectionVtbl *lpVtbl;
    };

    

#ifdef COBJMACROS


#define _Connection_QueryInterface(This,riid,ppvObject)	\
    ( (This)->lpVtbl -> QueryInterface(This,riid,ppvObject) ) 

#define _Connection_AddRef(This)	\
    ( (This)->lpVtbl -> AddRef(This) ) 

#define _Connection_Release(This)	\
    ( (This)->lpVtbl -> Release(This) ) 


#define _Connection_GetTypeInfoCount(This,pctinfo)	\
    ( (This)->lpVtbl -> GetTypeInfoCount(This,pctinfo) ) 

#define _Connection_GetTypeInfo(This,iTInfo,lcid,ppTInfo)	\
    ( (This)->lpVtbl -> GetTypeInfo(This,iTInfo,lcid,ppTInfo) ) 

#define _Connection_GetIDsOfNames(This,riid,rgszNames,cNames,lcid,rgDispId)	\
    ( (This)->lpVtbl -> GetIDsOfNames(This,riid,rgszNames,cNames,lcid,rgDispId) ) 

#define _Connection_Invoke(This,dispIdMember,riid,lcid,wFlags,pDispParams,pVarResult,pExcepInfo,puArgErr)	\
    ( (This)->lpVtbl -> Invoke(This,dispIdMember,riid,lcid,wFlags,pDispParams,pVarResult,pExcepInfo,puArgErr) ) 


#define _Connection_get_Properties(This,ppvObject)	\
    ( (This)->lpVtbl -> get_Properties(This,ppvObject) ) 


#define _Connection_get_ConnectionString(This,pbstr)	\
    ( (This)->lpVtbl -> get_ConnectionString(This,pbstr) ) 

#define _Connection_put_ConnectionString(This,pbstr)	\
    ( (This)->lpVtbl -> put_ConnectionString(This,pbstr) ) 

#define _Connection_get_CommandTimeout(This,plTimeout)	\
    ( (This)->lpVtbl -> get_CommandTimeout(This,plTimeout) ) 

#define _Connection_put_CommandTimeout(This,plTimeout)	\
    ( (This)->lpVtbl -> put_CommandTimeout(This,plTimeout) ) 

#define _Connection_get_ConnectionTimeout(This,plTimeout)	\
    ( (This)->lpVtbl -> get_ConnectionTimeout(This,plTimeout) ) 

#define _Connection_put_ConnectionTimeout(This,plTimeout)	\
    ( (This)->lpVtbl -> put_ConnectionTimeout(This,plTimeout) ) 

#define _Connection_get_Version(This,pbstr)	\
    ( (This)->lpVtbl -> get_Version(This,pbstr) ) 

#define _Connection_Close(This)	\
    ( (This)->lpVtbl -> Close(This) ) 

#define _Connection_Execute(This,CommandText,RecordsAffected,Options,ppiRset)	\
    ( (This)->lpVtbl -> Execute(This,CommandText,RecordsAffected,Options,ppiRset) ) 

#define _Connection_BeginTrans(This,TransactionLevel)	\
    ( (This)->lpVtbl -> BeginTrans(This,TransactionLevel) ) 

#define _Connection_CommitTrans(This)	\
    ( (This)->lpVtbl -> CommitTrans(This) ) 

#define _Connection_RollbackTrans(This)	\
    ( (This)->lpVtbl -> RollbackTrans(This) ) 

#define _Connection_Open(This,ConnectionString,UserID,Password,Options)	\
    ( (This)->lpVtbl -> Open(This,ConnectionString,UserID,Password,Options) ) 

#define _Connection_get_Errors(This,ppvObject)	\
    ( (This)->lpVtbl -> get_Errors(This,ppvObject) ) 

#define _Connection_get_DefaultDatabase(This,pbstr)	\
    ( (This)->lpVtbl -> get_DefaultDatabase(This,pbstr) ) 

#define _Connection_put_DefaultDatabase(This,pbstr)	\
    ( (This)->lpVtbl -> put_DefaultDatabase(This,pbstr) ) 

#define _Connection_get_IsolationLevel(This,Level)	\
    ( (This)->lpVtbl -> get_IsolationLevel(This,Level) ) 

#define _Connection_put_IsolationLevel(This,Level)	\
    ( (This)->lpVtbl -> put_IsolationLevel(This,Level) ) 

#define _Connection_get_Attributes(This,plAttr)	\
    ( (This)->lpVtbl -> get_Attributes(This,plAttr) ) 

#define _Connection_put_Attributes(This,plAttr)	\
    ( (This)->lpVtbl -> put_Attributes(This,plAttr) ) 

#define _Connection_get_CursorLocation(This,plCursorLoc)	\
    ( (This)->lpVtbl -> get_CursorLocation(This,plCursorLoc) ) 

#define _Connection_put_CursorLocation(This,plCursorLoc)	\
    ( (This)->lpVtbl -> put_CursorLocation(This,plCursorLoc) ) 

#define _Connection_get_Mode(This,plMode)	\
    ( (This)->lpVtbl -> get_Mode(This,plMode) ) 

#define _Connection_put_Mode(This,plMode)	\
    ( (This)->lpVtbl -> put_Mode(This,plMode) ) 

#define _Connection_get_Provider(This,pbstr)	\
    ( (This)->lpVtbl -> get_Provider(This,pbstr) ) 

#define _Connection_put_Provider(This,pbstr)	\
    ( (This)->lpVtbl -> put_Provider(This,pbstr) ) 

#define _Connection_get_State(This,plObjState)	\
    ( (This)->lpVtbl -> get_State(This,plObjState) ) 

#define _Connection_OpenSchema(This,Schema,Restrictions,SchemaID,pprset)	\
    ( (This)->lpVtbl -> OpenSchema(This,Schema,Restrictions,SchemaID,pprset) ) 


#define _Connection_Cancel(This)	\
    ( (This)->lpVtbl -> Cancel(This) ) 

#endif /* COBJMACROS */


#endif 	/* C style interface */




#endif 	/* ___Connection_INTERFACE_DEFINED__ */


#ifndef __Recordset15_INTERFACE_DEFINED__
#define __Recordset15_INTERFACE_DEFINED__

/* interface Recordset15 */
/* [object][oleautomation][nonextensible][dual][uuid] */ 


EXTERN_C const IID IID_Recordset15;

#if defined(__cplusplus) && !defined(CINTERFACE)
    
    MIDL_INTERFACE("0000050E-0000-0010-8000-00AA006D2EA4")
    Recordset15 : public _ADO
    {
    public:
        virtual /* [propget][id] */ HRESULT __stdcall get_AbsolutePosition( 
            /* [retval][out] */ PositionEnum_Param *pl) = 0;
        
        virtual /* [propput][id] */ HRESULT __stdcall put_AbsolutePosition( 
            /* [in] */ PositionEnum_Param pl) = 0;
        
        virtual /* [propputref][id] */ HRESULT __stdcall putref_ActiveConnection( 
            /* [in] */ IDispatch *pvar) = 0;
        
        virtual /* [propput][id] */ HRESULT __stdcall put_ActiveConnection( 
            /* [in] */ VARIANT pvar) = 0;
        
        virtual /* [propget][id] */ HRESULT __stdcall get_ActiveConnection( 
            /* [retval][out] */ VARIANT *pvar) = 0;
        
        virtual /* [propget][id] */ HRESULT __stdcall get_BOF( 
            /* [retval][out] */ VARIANT_BOOL *pb) = 0;
        
        virtual /* [propget][id] */ HRESULT __stdcall get_Bookmark( 
            /* [retval][out] */ VARIANT *pvBookmark) = 0;
        
        virtual /* [propput][id] */ HRESULT __stdcall put_Bookmark( 
            /* [in] */ VARIANT pvBookmark) = 0;
        
        virtual /* [propget][id] */ HRESULT __stdcall get_CacheSize( 
            /* [retval][out] */ long *pl) = 0;
        
        virtual /* [propput][id] */ HRESULT __stdcall put_CacheSize( 
            /* [in] */ long pl) = 0;
        
        virtual /* [propget][id] */ HRESULT __stdcall get_CursorType( 
            /* [retval][out] */ CursorTypeEnum *plCursorType) = 0;
        
        virtual /* [propput][id] */ HRESULT __stdcall put_CursorType( 
            /* [in] */ CursorTypeEnum plCursorType) = 0;
        
        virtual /* [propget][id] */ HRESULT __stdcall get_EOF( 
            /* [retval][out] */ VARIANT_BOOL *pb) = 0;
        
        virtual /* [propget][id] */ HRESULT __stdcall get_Fields( 
            /* [retval][out] */ Fields **ppvObject) = 0;
        
        virtual /* [propget][id] */ HRESULT __stdcall get_LockType( 
            /* [retval][out] */ LockTypeEnum *plLockType) = 0;
        
        virtual /* [propput][id] */ HRESULT __stdcall put_LockType( 
            /* [in] */ LockTypeEnum plLockType) = 0;
        
        virtual /* [propget][id] */ HRESULT __stdcall get_MaxRecords( 
            /* [retval][out] */ ADO_LONGPTR *plMaxRecords) = 0;
        
        virtual /* [propput][id] */ HRESULT __stdcall put_MaxRecords( 
            /* [in] */ ADO_LONGPTR plMaxRecords) = 0;
        
        virtual /* [propget][id] */ HRESULT __stdcall get_RecordCount( 
            /* [retval][out] */ ADO_LONGPTR *pl) = 0;
        
        virtual /* [propputref][id] */ HRESULT __stdcall putref_Source( 
            /* [in] */ IDispatch *pvSource) = 0;
        
        virtual /* [propput][id] */ HRESULT __stdcall put_Source( 
            /* [in] */ BSTR pvSource) = 0;
        
        virtual /* [propget][id] */ HRESULT __stdcall get_Source( 
            /* [retval][out] */ VARIANT *pvSource) = 0;
        
        virtual /* [id] */ HRESULT __stdcall AddNew( 
            /* [optional][in] */ VARIANT FieldList,
            /* [optional][in] */ VARIANT Values) = 0;
        
        virtual /* [id] */ HRESULT __stdcall CancelUpdate( void) = 0;
        
        virtual /* [id] */ HRESULT __stdcall Close( void) = 0;
        
        virtual /* [id] */ HRESULT __stdcall Delete( 
            /* [defaultvalue][in] */ AffectEnum AffectRecords = adAffectCurrent) = 0;
        
        virtual /* [id] */ HRESULT __stdcall GetRows( 
            /* [defaultvalue][in] */ long Rows,
            /* [optional][in] */ VARIANT Start,
            /* [optional][in] */ VARIANT Fields,
            /* [retval][out] */ VARIANT *pvar) = 0;
        
        virtual /* [id] */ HRESULT __stdcall Move( 
            /* [in] */ ADO_LONGPTR NumRecords,
            /* [optional][in] */ VARIANT Start) = 0;
        
        virtual /* [id] */ HRESULT __stdcall MoveNext( void) = 0;
        
        virtual /* [id] */ HRESULT __stdcall MovePrevious( void) = 0;
        
        virtual /* [id] */ HRESULT __stdcall MoveFirst( void) = 0;
        
        virtual /* [id] */ HRESULT __stdcall MoveLast( void) = 0;
        
        virtual /* [id] */ HRESULT __stdcall Open( 
            /* [optional][in] */ VARIANT Source,
            /* [optional][in] */ VARIANT ActiveConnection,
            /* [defaultvalue][in] */ CursorTypeEnum CursorType = adOpenUnspecified,
            /* [defaultvalue][in] */ LockTypeEnum LockType = adLockUnspecified,
            /* [defaultvalue][in] */ long Options = -1) = 0;
        
        virtual /* [id] */ HRESULT __stdcall Requery( 
            /* [defaultvalue][in] */ long Options = -1) = 0;
        
        virtual /* [hidden][id] */ HRESULT __stdcall _xResync( 
            /* [defaultvalue][in] */ AffectEnum AffectRecords = adAffectAll) = 0;
        
        virtual /* [id] */ HRESULT __stdcall Update( 
            /* [optional][in] */ VARIANT Fields,
            /* [optional][in] */ VARIANT Values) = 0;
        
        virtual /* [propget][id] */ HRESULT __stdcall get_AbsolutePage( 
            /* [retval][out] */ PositionEnum_Param *pl) = 0;
        
        virtual /* [propput][id] */ HRESULT __stdcall put_AbsolutePage( 
            /* [in] */ PositionEnum_Param pl) = 0;
        
        virtual /* [propget][id] */ HRESULT __stdcall get_EditMode( 
            /* [retval][out] */ EditModeEnum *pl) = 0;
        
        virtual /* [propget][id] */ HRESULT __stdcall get_Filter( 
            /* [retval][out] */ VARIANT *Criteria) = 0;
        
        virtual /* [propput][id] */ HRESULT __stdcall put_Filter( 
            /* [in] */ VARIANT Criteria) = 0;
        
        virtual /* [propget][id] */ HRESULT __stdcall get_PageCount( 
            /* [retval][out] */ ADO_LONGPTR *pl) = 0;
        
        virtual /* [propget][id] */ HRESULT __stdcall get_PageSize( 
            /* [retval][out] */ long *pl) = 0;
        
        virtual /* [propput][id] */ HRESULT __stdcall put_PageSize( 
            /* [in] */ long pl) = 0;
        
        virtual /* [propget][id] */ HRESULT __stdcall get_Sort( 
            /* [retval][out] */ BSTR *Criteria) = 0;
        
        virtual /* [propput][id] */ HRESULT __stdcall put_Sort( 
            /* [in] */ BSTR Criteria) = 0;
        
        virtual /* [propget][id] */ HRESULT __stdcall get_Status( 
            /* [retval][out] */ long *pl) = 0;
        
        virtual /* [propget][id] */ HRESULT __stdcall get_State( 
            /* [retval][out] */ long *plObjState) = 0;
        
        virtual /* [hidden][id] */ HRESULT __stdcall _xClone( 
            /* [retval][out] */ _Recordset **ppvObject) = 0;
        
        virtual /* [id] */ HRESULT __stdcall UpdateBatch( 
            /* [defaultvalue][in] */ AffectEnum AffectRecords = adAffectAll) = 0;
        
        virtual /* [id] */ HRESULT __stdcall CancelBatch( 
            /* [defaultvalue][in] */ AffectEnum AffectRecords = adAffectAll) = 0;
        
        virtual /* [propget][id] */ HRESULT __stdcall get_CursorLocation( 
            /* [retval][out] */ CursorLocationEnum *plCursorLoc) = 0;
        
        virtual /* [propput][id] */ HRESULT __stdcall put_CursorLocation( 
            /* [in] */ CursorLocationEnum plCursorLoc) = 0;
        
        virtual /* [id] */ HRESULT __stdcall NextRecordset( 
            /* [optional][out] */ VARIANT *RecordsAffected,
            /* [retval][out] */ _Recordset **ppiRs) = 0;
        
        virtual /* [id] */ HRESULT __stdcall Supports( 
            /* [in] */ CursorOptionEnum CursorOptions,
            /* [retval][out] */ VARIANT_BOOL *pb) = 0;
        
        virtual /* [hidden][propget][id] */ HRESULT __stdcall get_Collect( 
            /* [in] */ VARIANT Index,
            /* [retval][out] */ VARIANT *pvar) = 0;
        
        virtual /* [hidden][propput][id] */ HRESULT __stdcall put_Collect( 
            /* [in] */ VARIANT Index,
            /* [in] */ VARIANT pvar) = 0;
        
        virtual /* [propget][id] */ HRESULT __stdcall get_MarshalOptions( 
            /* [retval][out] */ MarshalOptionsEnum *peMarshal) = 0;
        
        virtual /* [propput][id] */ HRESULT __stdcall put_MarshalOptions( 
            /* [in] */ MarshalOptionsEnum peMarshal) = 0;
        
        virtual /* [id] */ HRESULT __stdcall Find( 
            /* [in] */ BSTR Criteria,
            /* [defaultvalue][in] */ ADO_LONGPTR SkipRecords,
            /* [defaultvalue][in] */ SearchDirectionEnum SearchDirection,
            /* [optional][in] */ VARIANT Start) = 0;
        
    };
    
#else 	/* C style interface */

    typedef struct Recordset15Vtbl
    {
        BEGIN_INTERFACE
        
        HRESULT ( STDMETHODCALLTYPE *QueryInterface )( 
            Recordset15 * This,
            /* [in] */ REFIID riid,
            /* [annotation][iid_is][out] */ 
            __RPC__deref_out  void **ppvObject);
        
        ULONG ( STDMETHODCALLTYPE *AddRef )( 
            Recordset15 * This);
        
        ULONG ( STDMETHODCALLTYPE *Release )( 
            Recordset15 * This);
        
        HRESULT ( STDMETHODCALLTYPE *GetTypeInfoCount )( 
            Recordset15 * This,
            /* [out] */ UINT *pctinfo);
        
        HRESULT ( STDMETHODCALLTYPE *GetTypeInfo )( 
            Recordset15 * This,
            /* [in] */ UINT iTInfo,
            /* [in] */ LCID lcid,
            /* [out] */ ITypeInfo **ppTInfo);
        
        HRESULT ( STDMETHODCALLTYPE *GetIDsOfNames )( 
            Recordset15 * This,
            /* [in] */ REFIID riid,
            /* [size_is][in] */ LPOLESTR *rgszNames,
            /* [range][in] */ UINT cNames,
            /* [in] */ LCID lcid,
            /* [size_is][out] */ DISPID *rgDispId);
        
        /* [local] */ HRESULT ( STDMETHODCALLTYPE *Invoke )( 
            Recordset15 * This,
            /* [in] */ DISPID dispIdMember,
            /* [in] */ REFIID riid,
            /* [in] */ LCID lcid,
            /* [in] */ WORD wFlags,
            /* [out][in] */ DISPPARAMS *pDispParams,
            /* [out] */ VARIANT *pVarResult,
            /* [out] */ EXCEPINFO *pExcepInfo,
            /* [out] */ UINT *puArgErr);
        
        /* [propget][id] */ HRESULT ( __stdcall *get_Properties )( 
            Recordset15 * This,
            /* [retval][out] */ Properties **ppvObject);
        
        /* [propget][id] */ HRESULT ( __stdcall *get_AbsolutePosition )( 
            Recordset15 * This,
            /* [retval][out] */ PositionEnum_Param *pl);
        
        /* [propput][id] */ HRESULT ( __stdcall *put_AbsolutePosition )( 
            Recordset15 * This,
            /* [in] */ PositionEnum_Param pl);
        
        /* [propputref][id] */ HRESULT ( __stdcall *putref_ActiveConnection )( 
            Recordset15 * This,
            /* [in] */ IDispatch *pvar);
        
        /* [propput][id] */ HRESULT ( __stdcall *put_ActiveConnection )( 
            Recordset15 * This,
            /* [in] */ VARIANT pvar);
        
        /* [propget][id] */ HRESULT ( __stdcall *get_ActiveConnection )( 
            Recordset15 * This,
            /* [retval][out] */ VARIANT *pvar);
        
        /* [propget][id] */ HRESULT ( __stdcall *get_BOF )( 
            Recordset15 * This,
            /* [retval][out] */ VARIANT_BOOL *pb);
        
        /* [propget][id] */ HRESULT ( __stdcall *get_Bookmark )( 
            Recordset15 * This,
            /* [retval][out] */ VARIANT *pvBookmark);
        
        /* [propput][id] */ HRESULT ( __stdcall *put_Bookmark )( 
            Recordset15 * This,
            /* [in] */ VARIANT pvBookmark);
        
        /* [propget][id] */ HRESULT ( __stdcall *get_CacheSize )( 
            Recordset15 * This,
            /* [retval][out] */ long *pl);
        
        /* [propput][id] */ HRESULT ( __stdcall *put_CacheSize )( 
            Recordset15 * This,
            /* [in] */ long pl);
        
        /* [propget][id] */ HRESULT ( __stdcall *get_CursorType )( 
            Recordset15 * This,
            /* [retval][out] */ CursorTypeEnum *plCursorType);
        
        /* [propput][id] */ HRESULT ( __stdcall *put_CursorType )( 
            Recordset15 * This,
            /* [in] */ CursorTypeEnum plCursorType);
        
        /* [propget][id] */ HRESULT ( __stdcall *get_EOF )( 
            Recordset15 * This,
            /* [retval][out] */ VARIANT_BOOL *pb);
        
        /* [propget][id] */ HRESULT ( __stdcall *get_Fields )( 
            Recordset15 * This,
            /* [retval][out] */ Fields **ppvObject);
        
        /* [propget][id] */ HRESULT ( __stdcall *get_LockType )( 
            Recordset15 * This,
            /* [retval][out] */ LockTypeEnum *plLockType);
        
        /* [propput][id] */ HRESULT ( __stdcall *put_LockType )( 
            Recordset15 * This,
            /* [in] */ LockTypeEnum plLockType);
        
        /* [propget][id] */ HRESULT ( __stdcall *get_MaxRecords )( 
            Recordset15 * This,
            /* [retval][out] */ ADO_LONGPTR *plMaxRecords);
        
        /* [propput][id] */ HRESULT ( __stdcall *put_MaxRecords )( 
            Recordset15 * This,
            /* [in] */ ADO_LONGPTR plMaxRecords);
        
        /* [propget][id] */ HRESULT ( __stdcall *get_RecordCount )( 
            Recordset15 * This,
            /* [retval][out] */ ADO_LONGPTR *pl);
        
        /* [propputref][id] */ HRESULT ( __stdcall *putref_Source )( 
            Recordset15 * This,
            /* [in] */ IDispatch *pvSource);
        
        /* [propput][id] */ HRESULT ( __stdcall *put_Source )( 
            Recordset15 * This,
            /* [in] */ BSTR pvSource);
        
        /* [propget][id] */ HRESULT ( __stdcall *get_Source )( 
            Recordset15 * This,
            /* [retval][out] */ VARIANT *pvSource);
        
        /* [id] */ HRESULT ( __stdcall *AddNew )( 
            Recordset15 * This,
            /* [optional][in] */ VARIANT FieldList,
            /* [optional][in] */ VARIANT Values);
        
        /* [id] */ HRESULT ( __stdcall *CancelUpdate )( 
            Recordset15 * This);
        
        /* [id] */ HRESULT ( __stdcall *Close )( 
            Recordset15 * This);
        
        /* [id] */ HRESULT ( __stdcall *Delete )( 
            Recordset15 * This,
            /* [defaultvalue][in] */ AffectEnum AffectRecords);
        
        /* [id] */ HRESULT ( __stdcall *GetRows )( 
            Recordset15 * This,
            /* [defaultvalue][in] */ long Rows,
            /* [optional][in] */ VARIANT Start,
            /* [optional][in] */ VARIANT Fields,
            /* [retval][out] */ VARIANT *pvar);
        
        /* [id] */ HRESULT ( __stdcall *Move )( 
            Recordset15 * This,
            /* [in] */ ADO_LONGPTR NumRecords,
            /* [optional][in] */ VARIANT Start);
        
        /* [id] */ HRESULT ( __stdcall *MoveNext )( 
            Recordset15 * This);
        
        /* [id] */ HRESULT ( __stdcall *MovePrevious )( 
            Recordset15 * This);
        
        /* [id] */ HRESULT ( __stdcall *MoveFirst )( 
            Recordset15 * This);
        
        /* [id] */ HRESULT ( __stdcall *MoveLast )( 
            Recordset15 * This);
        
        /* [id] */ HRESULT ( __stdcall *Open )( 
            Recordset15 * This,
            /* [optional][in] */ VARIANT Source,
            /* [optional][in] */ VARIANT ActiveConnection,
            /* [defaultvalue][in] */ CursorTypeEnum CursorType,
            /* [defaultvalue][in] */ LockTypeEnum LockType,
            /* [defaultvalue][in] */ long Options);
        
        /* [id] */ HRESULT ( __stdcall *Requery )( 
            Recordset15 * This,
            /* [defaultvalue][in] */ long Options);
        
        /* [hidden][id] */ HRESULT ( __stdcall *_xResync )( 
            Recordset15 * This,
            /* [defaultvalue][in] */ AffectEnum AffectRecords);
        
        /* [id] */ HRESULT ( __stdcall *Update )( 
            Recordset15 * This,
            /* [optional][in] */ VARIANT Fields,
            /* [optional][in] */ VARIANT Values);
        
        /* [propget][id] */ HRESULT ( __stdcall *get_AbsolutePage )( 
            Recordset15 * This,
            /* [retval][out] */ PositionEnum_Param *pl);
        
        /* [propput][id] */ HRESULT ( __stdcall *put_AbsolutePage )( 
            Recordset15 * This,
            /* [in] */ PositionEnum_Param pl);
        
        /* [propget][id] */ HRESULT ( __stdcall *get_EditMode )( 
            Recordset15 * This,
            /* [retval][out] */ EditModeEnum *pl);
        
        /* [propget][id] */ HRESULT ( __stdcall *get_Filter )( 
            Recordset15 * This,
            /* [retval][out] */ VARIANT *Criteria);
        
        /* [propput][id] */ HRESULT ( __stdcall *put_Filter )( 
            Recordset15 * This,
            /* [in] */ VARIANT Criteria);
        
        /* [propget][id] */ HRESULT ( __stdcall *get_PageCount )( 
            Recordset15 * This,
            /* [retval][out] */ ADO_LONGPTR *pl);
        
        /* [propget][id] */ HRESULT ( __stdcall *get_PageSize )( 
            Recordset15 * This,
            /* [retval][out] */ long *pl);
        
        /* [propput][id] */ HRESULT ( __stdcall *put_PageSize )( 
            Recordset15 * This,
            /* [in] */ long pl);
        
        /* [propget][id] */ HRESULT ( __stdcall *get_Sort )( 
            Recordset15 * This,
            /* [retval][out] */ BSTR *Criteria);
        
        /* [propput][id] */ HRESULT ( __stdcall *put_Sort )( 
            Recordset15 * This,
            /* [in] */ BSTR Criteria);
        
        /* [propget][id] */ HRESULT ( __stdcall *get_Status )( 
            Recordset15 * This,
            /* [retval][out] */ long *pl);
        
        /* [propget][id] */ HRESULT ( __stdcall *get_State )( 
            Recordset15 * This,
            /* [retval][out] */ long *plObjState);
        
        /* [hidden][id] */ HRESULT ( __stdcall *_xClone )( 
            Recordset15 * This,
            /* [retval][out] */ _Recordset **ppvObject);
        
        /* [id] */ HRESULT ( __stdcall *UpdateBatch )( 
            Recordset15 * This,
            /* [defaultvalue][in] */ AffectEnum AffectRecords);
        
        /* [id] */ HRESULT ( __stdcall *CancelBatch )( 
            Recordset15 * This,
            /* [defaultvalue][in] */ AffectEnum AffectRecords);
        
        /* [propget][id] */ HRESULT ( __stdcall *get_CursorLocation )( 
            Recordset15 * This,
            /* [retval][out] */ CursorLocationEnum *plCursorLoc);
        
        /* [propput][id] */ HRESULT ( __stdcall *put_CursorLocation )( 
            Recordset15 * This,
            /* [in] */ CursorLocationEnum plCursorLoc);
        
        /* [id] */ HRESULT ( __stdcall *NextRecordset )( 
            Recordset15 * This,
            /* [optional][out] */ VARIANT *RecordsAffected,
            /* [retval][out] */ _Recordset **ppiRs);
        
        /* [id] */ HRESULT ( __stdcall *Supports )( 
            Recordset15 * This,
            /* [in] */ CursorOptionEnum CursorOptions,
            /* [retval][out] */ VARIANT_BOOL *pb);
        
        /* [hidden][propget][id] */ HRESULT ( __stdcall *get_Collect )( 
            Recordset15 * This,
            /* [in] */ VARIANT Index,
            /* [retval][out] */ VARIANT *pvar);
        
        /* [hidden][propput][id] */ HRESULT ( __stdcall *put_Collect )( 
            Recordset15 * This,
            /* [in] */ VARIANT Index,
            /* [in] */ VARIANT pvar);
        
        /* [propget][id] */ HRESULT ( __stdcall *get_MarshalOptions )( 
            Recordset15 * This,
            /* [retval][out] */ MarshalOptionsEnum *peMarshal);
        
        /* [propput][id] */ HRESULT ( __stdcall *put_MarshalOptions )( 
            Recordset15 * This,
            /* [in] */ MarshalOptionsEnum peMarshal);
        
        /* [id] */ HRESULT ( __stdcall *Find )( 
            Recordset15 * This,
            /* [in] */ BSTR Criteria,
            /* [defaultvalue][in] */ ADO_LONGPTR SkipRecords,
            /* [defaultvalue][in] */ SearchDirectionEnum SearchDirection,
            /* [optional][in] */ VARIANT Start);
        
        END_INTERFACE
    } Recordset15Vtbl;

    interface Recordset15
    {
        CONST_VTBL struct Recordset15Vtbl *lpVtbl;
    };

    

#ifdef COBJMACROS


#define Recordset15_QueryInterface(This,riid,ppvObject)	\
    ( (This)->lpVtbl -> QueryInterface(This,riid,ppvObject) ) 

#define Recordset15_AddRef(This)	\
    ( (This)->lpVtbl -> AddRef(This) ) 

#define Recordset15_Release(This)	\
    ( (This)->lpVtbl -> Release(This) ) 


#define Recordset15_GetTypeInfoCount(This,pctinfo)	\
    ( (This)->lpVtbl -> GetTypeInfoCount(This,pctinfo) ) 

#define Recordset15_GetTypeInfo(This,iTInfo,lcid,ppTInfo)	\
    ( (This)->lpVtbl -> GetTypeInfo(This,iTInfo,lcid,ppTInfo) ) 

#define Recordset15_GetIDsOfNames(This,riid,rgszNames,cNames,lcid,rgDispId)	\
    ( (This)->lpVtbl -> GetIDsOfNames(This,riid,rgszNames,cNames,lcid,rgDispId) ) 

#define Recordset15_Invoke(This,dispIdMember,riid,lcid,wFlags,pDispParams,pVarResult,pExcepInfo,puArgErr)	\
    ( (This)->lpVtbl -> Invoke(This,dispIdMember,riid,lcid,wFlags,pDispParams,pVarResult,pExcepInfo,puArgErr) ) 


#define Recordset15_get_Properties(This,ppvObject)	\
    ( (This)->lpVtbl -> get_Properties(This,ppvObject) ) 


#define Recordset15_get_AbsolutePosition(This,pl)	\
    ( (This)->lpVtbl -> get_AbsolutePosition(This,pl) ) 

#define Recordset15_put_AbsolutePosition(This,pl)	\
    ( (This)->lpVtbl -> put_AbsolutePosition(This,pl) ) 

#define Recordset15_putref_ActiveConnection(This,pvar)	\
    ( (This)->lpVtbl -> putref_ActiveConnection(This,pvar) ) 

#define Recordset15_put_ActiveConnection(This,pvar)	\
    ( (This)->lpVtbl -> put_ActiveConnection(This,pvar) ) 

#define Recordset15_get_ActiveConnection(This,pvar)	\
    ( (This)->lpVtbl -> get_ActiveConnection(This,pvar) ) 

#define Recordset15_get_BOF(This,pb)	\
    ( (This)->lpVtbl -> get_BOF(This,pb) ) 

#define Recordset15_get_Bookmark(This,pvBookmark)	\
    ( (This)->lpVtbl -> get_Bookmark(This,pvBookmark) ) 

#define Recordset15_put_Bookmark(This,pvBookmark)	\
    ( (This)->lpVtbl -> put_Bookmark(This,pvBookmark) ) 

#define Recordset15_get_CacheSize(This,pl)	\
    ( (This)->lpVtbl -> get_CacheSize(This,pl) ) 

#define Recordset15_put_CacheSize(This,pl)	\
    ( (This)->lpVtbl -> put_CacheSize(This,pl) ) 

#define Recordset15_get_CursorType(This,plCursorType)	\
    ( (This)->lpVtbl -> get_CursorType(This,plCursorType) ) 

#define Recordset15_put_CursorType(This,plCursorType)	\
    ( (This)->lpVtbl -> put_CursorType(This,plCursorType) ) 

#define Recordset15_get_EOF(This,pb)	\
    ( (This)->lpVtbl -> get_EOF(This,pb) ) 

#define Recordset15_get_Fields(This,ppvObject)	\
    ( (This)->lpVtbl -> get_Fields(This,ppvObject) ) 

#define Recordset15_get_LockType(This,plLockType)	\
    ( (This)->lpVtbl -> get_LockType(This,plLockType) ) 

#define Recordset15_put_LockType(This,plLockType)	\
    ( (This)->lpVtbl -> put_LockType(This,plLockType) ) 

#define Recordset15_get_MaxRecords(This,plMaxRecords)	\
    ( (This)->lpVtbl -> get_MaxRecords(This,plMaxRecords) ) 

#define Recordset15_put_MaxRecords(This,plMaxRecords)	\
    ( (This)->lpVtbl -> put_MaxRecords(This,plMaxRecords) ) 

#define Recordset15_get_RecordCount(This,pl)	\
    ( (This)->lpVtbl -> get_RecordCount(This,pl) ) 

#define Recordset15_putref_Source(This,pvSource)	\
    ( (This)->lpVtbl -> putref_Source(This,pvSource) ) 

#define Recordset15_put_Source(This,pvSource)	\
    ( (This)->lpVtbl -> put_Source(This,pvSource) ) 

#define Recordset15_get_Source(This,pvSource)	\
    ( (This)->lpVtbl -> get_Source(This,pvSource) ) 

#define Recordset15_AddNew(This,FieldList,Values)	\
    ( (This)->lpVtbl -> AddNew(This,FieldList,Values) ) 

#define Recordset15_CancelUpdate(This)	\
    ( (This)->lpVtbl -> CancelUpdate(This) ) 

#define Recordset15_Close(This)	\
    ( (This)->lpVtbl -> Close(This) ) 

#define Recordset15_Delete(This,AffectRecords)	\
    ( (This)->lpVtbl -> Delete(This,AffectRecords) ) 

#define Recordset15_GetRows(This,Rows,Start,Fields,pvar)	\
    ( (This)->lpVtbl -> GetRows(This,Rows,Start,Fields,pvar) ) 

#define Recordset15_Move(This,NumRecords,Start)	\
    ( (This)->lpVtbl -> Move(This,NumRecords,Start) ) 

#define Recordset15_MoveNext(This)	\
    ( (This)->lpVtbl -> MoveNext(This) ) 

#define Recordset15_MovePrevious(This)	\
    ( (This)->lpVtbl -> MovePrevious(This) ) 

#define Recordset15_MoveFirst(This)	\
    ( (This)->lpVtbl -> MoveFirst(This) ) 

#define Recordset15_MoveLast(This)	\
    ( (This)->lpVtbl -> MoveLast(This) ) 

#define Recordset15_Open(This,Source,ActiveConnection,CursorType,LockType,Options)	\
    ( (This)->lpVtbl -> Open(This,Source,ActiveConnection,CursorType,LockType,Options) ) 

#define Recordset15_Requery(This,Options)	\
    ( (This)->lpVtbl -> Requery(This,Options) ) 

#define Recordset15__xResync(This,AffectRecords)	\
    ( (This)->lpVtbl -> _xResync(This,AffectRecords) ) 

#define Recordset15_Update(This,Fields,Values)	\
    ( (This)->lpVtbl -> Update(This,Fields,Values) ) 

#define Recordset15_get_AbsolutePage(This,pl)	\
    ( (This)->lpVtbl -> get_AbsolutePage(This,pl) ) 

#define Recordset15_put_AbsolutePage(This,pl)	\
    ( (This)->lpVtbl -> put_AbsolutePage(This,pl) ) 

#define Recordset15_get_EditMode(This,pl)	\
    ( (This)->lpVtbl -> get_EditMode(This,pl) ) 

#define Recordset15_get_Filter(This,Criteria)	\
    ( (This)->lpVtbl -> get_Filter(This,Criteria) ) 

#define Recordset15_put_Filter(This,Criteria)	\
    ( (This)->lpVtbl -> put_Filter(This,Criteria) ) 

#define Recordset15_get_PageCount(This,pl)	\
    ( (This)->lpVtbl -> get_PageCount(This,pl) ) 

#define Recordset15_get_PageSize(This,pl)	\
    ( (This)->lpVtbl -> get_PageSize(This,pl) ) 

#define Recordset15_put_PageSize(This,pl)	\
    ( (This)->lpVtbl -> put_PageSize(This,pl) ) 

#define Recordset15_get_Sort(This,Criteria)	\
    ( (This)->lpVtbl -> get_Sort(This,Criteria) ) 

#define Recordset15_put_Sort(This,Criteria)	\
    ( (This)->lpVtbl -> put_Sort(This,Criteria) ) 

#define Recordset15_get_Status(This,pl)	\
    ( (This)->lpVtbl -> get_Status(This,pl) ) 

#define Recordset15_get_State(This,plObjState)	\
    ( (This)->lpVtbl -> get_State(This,plObjState) ) 

#define Recordset15__xClone(This,ppvObject)	\
    ( (This)->lpVtbl -> _xClone(This,ppvObject) ) 

#define Recordset15_UpdateBatch(This,AffectRecords)	\
    ( (This)->lpVtbl -> UpdateBatch(This,AffectRecords) ) 

#define Recordset15_CancelBatch(This,AffectRecords)	\
    ( (This)->lpVtbl -> CancelBatch(This,AffectRecords) ) 

#define Recordset15_get_CursorLocation(This,plCursorLoc)	\
    ( (This)->lpVtbl -> get_CursorLocation(This,plCursorLoc) ) 

#define Recordset15_put_CursorLocation(This,plCursorLoc)	\
    ( (This)->lpVtbl -> put_CursorLocation(This,plCursorLoc) ) 

#define Recordset15_NextRecordset(This,RecordsAffected,ppiRs)	\
    ( (This)->lpVtbl -> NextRecordset(This,RecordsAffected,ppiRs) ) 

#define Recordset15_Supports(This,CursorOptions,pb)	\
    ( (This)->lpVtbl -> Supports(This,CursorOptions,pb) ) 

#define Recordset15_get_Collect(This,Index,pvar)	\
    ( (This)->lpVtbl -> get_Collect(This,Index,pvar) ) 

#define Recordset15_put_Collect(This,Index,pvar)	\
    ( (This)->lpVtbl -> put_Collect(This,Index,pvar) ) 

#define Recordset15_get_MarshalOptions(This,peMarshal)	\
    ( (This)->lpVtbl -> get_MarshalOptions(This,peMarshal) ) 

#define Recordset15_put_MarshalOptions(This,peMarshal)	\
    ( (This)->lpVtbl -> put_MarshalOptions(This,peMarshal) ) 

#define Recordset15_Find(This,Criteria,SkipRecords,SearchDirection,Start)	\
    ( (This)->lpVtbl -> Find(This,Criteria,SkipRecords,SearchDirection,Start) ) 

#endif /* COBJMACROS */


#endif 	/* C style interface */




#endif 	/* __Recordset15_INTERFACE_DEFINED__ */


#ifndef __Recordset20_INTERFACE_DEFINED__
#define __Recordset20_INTERFACE_DEFINED__

/* interface Recordset20 */
/* [object][oleautomation][nonextensible][dual][uuid] */ 


EXTERN_C const IID IID_Recordset20;

#if defined(__cplusplus) && !defined(CINTERFACE)
    
    MIDL_INTERFACE("0000054F-0000-0010-8000-00AA006D2EA4")
    Recordset20 : public Recordset15
    {
    public:
        virtual /* [id] */ HRESULT __stdcall Cancel( void) = 0;
        
        virtual /* [propget][id] */ HRESULT __stdcall get_DataSource( 
            /* [retval][out] */ IUnknown **ppunkDataSource) = 0;
        
        virtual /* [propputref][id] */ HRESULT __stdcall putref_DataSource( 
            /* [in] */ IUnknown *ppunkDataSource) = 0;
        
        virtual /* [hidden] */ HRESULT __stdcall _xSave( 
            /* [optional][in] */ BSTR FileName,
            /* [defaultvalue][in] */ PersistFormatEnum PersistFormat = adPersistADTG) = 0;
        
        virtual /* [propget][id] */ HRESULT __stdcall get_ActiveCommand( 
            /* [retval][out] */ IDispatch **ppCmd) = 0;
        
        virtual /* [propput][id] */ HRESULT __stdcall put_StayInSync( 
            /* [in] */ VARIANT_BOOL pbStayInSync) = 0;
        
        virtual /* [propget][id] */ HRESULT __stdcall get_StayInSync( 
            /* [retval][out] */ VARIANT_BOOL *pbStayInSync) = 0;
        
        virtual /* [id] */ HRESULT __stdcall GetString( 
            /* [defaultvalue][in] */ StringFormatEnum StringFormat,
            /* [defaultvalue][in] */ long NumRows,
            /* [optional][in] */ BSTR ColumnDelimeter,
            /* [optional][in] */ BSTR RowDelimeter,
            /* [optional][in] */ BSTR NullExpr,
            /* [retval][out] */ BSTR *pRetString) = 0;
        
        virtual /* [propget][id] */ HRESULT __stdcall get_DataMember( 
            /* [retval][out] */ BSTR *pbstrDataMember) = 0;
        
        virtual /* [propput][id] */ HRESULT __stdcall put_DataMember( 
            /* [in] */ BSTR pbstrDataMember) = 0;
        
        virtual /* [id] */ HRESULT __stdcall CompareBookmarks( 
            /* [in] */ VARIANT Bookmark1,
            /* [in] */ VARIANT Bookmark2,
            /* [retval][out] */ CompareEnum *pCompare) = 0;
        
        virtual /* [id] */ HRESULT __stdcall Clone( 
            /* [defaultvalue][in] */ LockTypeEnum LockType,
            /* [retval][out] */ _Recordset **ppvObject) = 0;
        
        virtual /* [id] */ HRESULT __stdcall Resync( 
            /* [defaultvalue][in] */ AffectEnum AffectRecords = adAffectAll,
            /* [defaultvalue][in] */ ResyncEnum ResyncValues = adResyncAllValues) = 0;
        
    };
    
#else 	/* C style interface */

    typedef struct Recordset20Vtbl
    {
        BEGIN_INTERFACE
        
        HRESULT ( STDMETHODCALLTYPE *QueryInterface )( 
            Recordset20 * This,
            /* [in] */ REFIID riid,
            /* [annotation][iid_is][out] */ 
            __RPC__deref_out  void **ppvObject);
        
        ULONG ( STDMETHODCALLTYPE *AddRef )( 
            Recordset20 * This);
        
        ULONG ( STDMETHODCALLTYPE *Release )( 
            Recordset20 * This);
        
        HRESULT ( STDMETHODCALLTYPE *GetTypeInfoCount )( 
            Recordset20 * This,
            /* [out] */ UINT *pctinfo);
        
        HRESULT ( STDMETHODCALLTYPE *GetTypeInfo )( 
            Recordset20 * This,
            /* [in] */ UINT iTInfo,
            /* [in] */ LCID lcid,
            /* [out] */ ITypeInfo **ppTInfo);
        
        HRESULT ( STDMETHODCALLTYPE *GetIDsOfNames )( 
            Recordset20 * This,
            /* [in] */ REFIID riid,
            /* [size_is][in] */ LPOLESTR *rgszNames,
            /* [range][in] */ UINT cNames,
            /* [in] */ LCID lcid,
            /* [size_is][out] */ DISPID *rgDispId);
        
        /* [local] */ HRESULT ( STDMETHODCALLTYPE *Invoke )( 
            Recordset20 * This,
            /* [in] */ DISPID dispIdMember,
            /* [in] */ REFIID riid,
            /* [in] */ LCID lcid,
            /* [in] */ WORD wFlags,
            /* [out][in] */ DISPPARAMS *pDispParams,
            /* [out] */ VARIANT *pVarResult,
            /* [out] */ EXCEPINFO *pExcepInfo,
            /* [out] */ UINT *puArgErr);
        
        /* [propget][id] */ HRESULT ( __stdcall *get_Properties )( 
            Recordset20 * This,
            /* [retval][out] */ Properties **ppvObject);
        
        /* [propget][id] */ HRESULT ( __stdcall *get_AbsolutePosition )( 
            Recordset20 * This,
            /* [retval][out] */ PositionEnum_Param *pl);
        
        /* [propput][id] */ HRESULT ( __stdcall *put_AbsolutePosition )( 
            Recordset20 * This,
            /* [in] */ PositionEnum_Param pl);
        
        /* [propputref][id] */ HRESULT ( __stdcall *putref_ActiveConnection )( 
            Recordset20 * This,
            /* [in] */ IDispatch *pvar);
        
        /* [propput][id] */ HRESULT ( __stdcall *put_ActiveConnection )( 
            Recordset20 * This,
            /* [in] */ VARIANT pvar);
        
        /* [propget][id] */ HRESULT ( __stdcall *get_ActiveConnection )( 
            Recordset20 * This,
            /* [retval][out] */ VARIANT *pvar);
        
        /* [propget][id] */ HRESULT ( __stdcall *get_BOF )( 
            Recordset20 * This,
            /* [retval][out] */ VARIANT_BOOL *pb);
        
        /* [propget][id] */ HRESULT ( __stdcall *get_Bookmark )( 
            Recordset20 * This,
            /* [retval][out] */ VARIANT *pvBookmark);
        
        /* [propput][id] */ HRESULT ( __stdcall *put_Bookmark )( 
            Recordset20 * This,
            /* [in] */ VARIANT pvBookmark);
        
        /* [propget][id] */ HRESULT ( __stdcall *get_CacheSize )( 
            Recordset20 * This,
            /* [retval][out] */ long *pl);
        
        /* [propput][id] */ HRESULT ( __stdcall *put_CacheSize )( 
            Recordset20 * This,
            /* [in] */ long pl);
        
        /* [propget][id] */ HRESULT ( __stdcall *get_CursorType )( 
            Recordset20 * This,
            /* [retval][out] */ CursorTypeEnum *plCursorType);
        
        /* [propput][id] */ HRESULT ( __stdcall *put_CursorType )( 
            Recordset20 * This,
            /* [in] */ CursorTypeEnum plCursorType);
        
        /* [propget][id] */ HRESULT ( __stdcall *get_EOF )( 
            Recordset20 * This,
            /* [retval][out] */ VARIANT_BOOL *pb);
        
        /* [propget][id] */ HRESULT ( __stdcall *get_Fields )( 
            Recordset20 * This,
            /* [retval][out] */ Fields **ppvObject);
        
        /* [propget][id] */ HRESULT ( __stdcall *get_LockType )( 
            Recordset20 * This,
            /* [retval][out] */ LockTypeEnum *plLockType);
        
        /* [propput][id] */ HRESULT ( __stdcall *put_LockType )( 
            Recordset20 * This,
            /* [in] */ LockTypeEnum plLockType);
        
        /* [propget][id] */ HRESULT ( __stdcall *get_MaxRecords )( 
            Recordset20 * This,
            /* [retval][out] */ ADO_LONGPTR *plMaxRecords);
        
        /* [propput][id] */ HRESULT ( __stdcall *put_MaxRecords )( 
            Recordset20 * This,
            /* [in] */ ADO_LONGPTR plMaxRecords);
        
        /* [propget][id] */ HRESULT ( __stdcall *get_RecordCount )( 
            Recordset20 * This,
            /* [retval][out] */ ADO_LONGPTR *pl);
        
        /* [propputref][id] */ HRESULT ( __stdcall *putref_Source )( 
            Recordset20 * This,
            /* [in] */ IDispatch *pvSource);
        
        /* [propput][id] */ HRESULT ( __stdcall *put_Source )( 
            Recordset20 * This,
            /* [in] */ BSTR pvSource);
        
        /* [propget][id] */ HRESULT ( __stdcall *get_Source )( 
            Recordset20 * This,
            /* [retval][out] */ VARIANT *pvSource);
        
        /* [id] */ HRESULT ( __stdcall *AddNew )( 
            Recordset20 * This,
            /* [optional][in] */ VARIANT FieldList,
            /* [optional][in] */ VARIANT Values);
        
        /* [id] */ HRESULT ( __stdcall *CancelUpdate )( 
            Recordset20 * This);
        
        /* [id] */ HRESULT ( __stdcall *Close )( 
            Recordset20 * This);
        
        /* [id] */ HRESULT ( __stdcall *Delete )( 
            Recordset20 * This,
            /* [defaultvalue][in] */ AffectEnum AffectRecords);
        
        /* [id] */ HRESULT ( __stdcall *GetRows )( 
            Recordset20 * This,
            /* [defaultvalue][in] */ long Rows,
            /* [optional][in] */ VARIANT Start,
            /* [optional][in] */ VARIANT Fields,
            /* [retval][out] */ VARIANT *pvar);
        
        /* [id] */ HRESULT ( __stdcall *Move )( 
            Recordset20 * This,
            /* [in] */ ADO_LONGPTR NumRecords,
            /* [optional][in] */ VARIANT Start);
        
        /* [id] */ HRESULT ( __stdcall *MoveNext )( 
            Recordset20 * This);
        
        /* [id] */ HRESULT ( __stdcall *MovePrevious )( 
            Recordset20 * This);
        
        /* [id] */ HRESULT ( __stdcall *MoveFirst )( 
            Recordset20 * This);
        
        /* [id] */ HRESULT ( __stdcall *MoveLast )( 
            Recordset20 * This);
        
        /* [id] */ HRESULT ( __stdcall *Open )( 
            Recordset20 * This,
            /* [optional][in] */ VARIANT Source,
            /* [optional][in] */ VARIANT ActiveConnection,
            /* [defaultvalue][in] */ CursorTypeEnum CursorType,
            /* [defaultvalue][in] */ LockTypeEnum LockType,
            /* [defaultvalue][in] */ long Options);
        
        /* [id] */ HRESULT ( __stdcall *Requery )( 
            Recordset20 * This,
            /* [defaultvalue][in] */ long Options);
        
        /* [hidden][id] */ HRESULT ( __stdcall *_xResync )( 
            Recordset20 * This,
            /* [defaultvalue][in] */ AffectEnum AffectRecords);
        
        /* [id] */ HRESULT ( __stdcall *Update )( 
            Recordset20 * This,
            /* [optional][in] */ VARIANT Fields,
            /* [optional][in] */ VARIANT Values);
        
        /* [propget][id] */ HRESULT ( __stdcall *get_AbsolutePage )( 
            Recordset20 * This,
            /* [retval][out] */ PositionEnum_Param *pl);
        
        /* [propput][id] */ HRESULT ( __stdcall *put_AbsolutePage )( 
            Recordset20 * This,
            /* [in] */ PositionEnum_Param pl);
        
        /* [propget][id] */ HRESULT ( __stdcall *get_EditMode )( 
            Recordset20 * This,
            /* [retval][out] */ EditModeEnum *pl);
        
        /* [propget][id] */ HRESULT ( __stdcall *get_Filter )( 
            Recordset20 * This,
            /* [retval][out] */ VARIANT *Criteria);
        
        /* [propput][id] */ HRESULT ( __stdcall *put_Filter )( 
            Recordset20 * This,
            /* [in] */ VARIANT Criteria);
        
        /* [propget][id] */ HRESULT ( __stdcall *get_PageCount )( 
            Recordset20 * This,
            /* [retval][out] */ ADO_LONGPTR *pl);
        
        /* [propget][id] */ HRESULT ( __stdcall *get_PageSize )( 
            Recordset20 * This,
            /* [retval][out] */ long *pl);
        
        /* [propput][id] */ HRESULT ( __stdcall *put_PageSize )( 
            Recordset20 * This,
            /* [in] */ long pl);
        
        /* [propget][id] */ HRESULT ( __stdcall *get_Sort )( 
            Recordset20 * This,
            /* [retval][out] */ BSTR *Criteria);
        
        /* [propput][id] */ HRESULT ( __stdcall *put_Sort )( 
            Recordset20 * This,
            /* [in] */ BSTR Criteria);
        
        /* [propget][id] */ HRESULT ( __stdcall *get_Status )( 
            Recordset20 * This,
            /* [retval][out] */ long *pl);
        
        /* [propget][id] */ HRESULT ( __stdcall *get_State )( 
            Recordset20 * This,
            /* [retval][out] */ long *plObjState);
        
        /* [hidden][id] */ HRESULT ( __stdcall *_xClone )( 
            Recordset20 * This,
            /* [retval][out] */ _Recordset **ppvObject);
        
        /* [id] */ HRESULT ( __stdcall *UpdateBatch )( 
            Recordset20 * This,
            /* [defaultvalue][in] */ AffectEnum AffectRecords);
        
        /* [id] */ HRESULT ( __stdcall *CancelBatch )( 
            Recordset20 * This,
            /* [defaultvalue][in] */ AffectEnum AffectRecords);
        
        /* [propget][id] */ HRESULT ( __stdcall *get_CursorLocation )( 
            Recordset20 * This,
            /* [retval][out] */ CursorLocationEnum *plCursorLoc);
        
        /* [propput][id] */ HRESULT ( __stdcall *put_CursorLocation )( 
            Recordset20 * This,
            /* [in] */ CursorLocationEnum plCursorLoc);
        
        /* [id] */ HRESULT ( __stdcall *NextRecordset )( 
            Recordset20 * This,
            /* [optional][out] */ VARIANT *RecordsAffected,
            /* [retval][out] */ _Recordset **ppiRs);
        
        /* [id] */ HRESULT ( __stdcall *Supports )( 
            Recordset20 * This,
            /* [in] */ CursorOptionEnum CursorOptions,
            /* [retval][out] */ VARIANT_BOOL *pb);
        
        /* [hidden][propget][id] */ HRESULT ( __stdcall *get_Collect )( 
            Recordset20 * This,
            /* [in] */ VARIANT Index,
            /* [retval][out] */ VARIANT *pvar);
        
        /* [hidden][propput][id] */ HRESULT ( __stdcall *put_Collect )( 
            Recordset20 * This,
            /* [in] */ VARIANT Index,
            /* [in] */ VARIANT pvar);
        
        /* [propget][id] */ HRESULT ( __stdcall *get_MarshalOptions )( 
            Recordset20 * This,
            /* [retval][out] */ MarshalOptionsEnum *peMarshal);
        
        /* [propput][id] */ HRESULT ( __stdcall *put_MarshalOptions )( 
            Recordset20 * This,
            /* [in] */ MarshalOptionsEnum peMarshal);
        
        /* [id] */ HRESULT ( __stdcall *Find )( 
            Recordset20 * This,
            /* [in] */ BSTR Criteria,
            /* [defaultvalue][in] */ ADO_LONGPTR SkipRecords,
            /* [defaultvalue][in] */ SearchDirectionEnum SearchDirection,
            /* [optional][in] */ VARIANT Start);
        
        /* [id] */ HRESULT ( __stdcall *Cancel )( 
            Recordset20 * This);
        
        /* [propget][id] */ HRESULT ( __stdcall *get_DataSource )( 
            Recordset20 * This,
            /* [retval][out] */ IUnknown **ppunkDataSource);
        
        /* [propputref][id] */ HRESULT ( __stdcall *putref_DataSource )( 
            Recordset20 * This,
            /* [in] */ IUnknown *ppunkDataSource);
        
        /* [hidden] */ HRESULT ( __stdcall *_xSave )( 
            Recordset20 * This,
            /* [optional][in] */ BSTR FileName,
            /* [defaultvalue][in] */ PersistFormatEnum PersistFormat);
        
        /* [propget][id] */ HRESULT ( __stdcall *get_ActiveCommand )( 
            Recordset20 * This,
            /* [retval][out] */ IDispatch **ppCmd);
        
        /* [propput][id] */ HRESULT ( __stdcall *put_StayInSync )( 
            Recordset20 * This,
            /* [in] */ VARIANT_BOOL pbStayInSync);
        
        /* [propget][id] */ HRESULT ( __stdcall *get_StayInSync )( 
            Recordset20 * This,
            /* [retval][out] */ VARIANT_BOOL *pbStayInSync);
        
        /* [id] */ HRESULT ( __stdcall *GetString )( 
            Recordset20 * This,
            /* [defaultvalue][in] */ StringFormatEnum StringFormat,
            /* [defaultvalue][in] */ long NumRows,
            /* [optional][in] */ BSTR ColumnDelimeter,
            /* [optional][in] */ BSTR RowDelimeter,
            /* [optional][in] */ BSTR NullExpr,
            /* [retval][out] */ BSTR *pRetString);
        
        /* [propget][id] */ HRESULT ( __stdcall *get_DataMember )( 
            Recordset20 * This,
            /* [retval][out] */ BSTR *pbstrDataMember);
        
        /* [propput][id] */ HRESULT ( __stdcall *put_DataMember )( 
            Recordset20 * This,
            /* [in] */ BSTR pbstrDataMember);
        
        /* [id] */ HRESULT ( __stdcall *CompareBookmarks )( 
            Recordset20 * This,
            /* [in] */ VARIANT Bookmark1,
            /* [in] */ VARIANT Bookmark2,
            /* [retval][out] */ CompareEnum *pCompare);
        
        /* [id] */ HRESULT ( __stdcall *Clone )( 
            Recordset20 * This,
            /* [defaultvalue][in] */ LockTypeEnum LockType,
            /* [retval][out] */ _Recordset **ppvObject);
        
        /* [id] */ HRESULT ( __stdcall *Resync )( 
            Recordset20 * This,
            /* [defaultvalue][in] */ AffectEnum AffectRecords,
            /* [defaultvalue][in] */ ResyncEnum ResyncValues);
        
        END_INTERFACE
    } Recordset20Vtbl;

    interface Recordset20
    {
        CONST_VTBL struct Recordset20Vtbl *lpVtbl;
    };

    

#ifdef COBJMACROS


#define Recordset20_QueryInterface(This,riid,ppvObject)	\
    ( (This)->lpVtbl -> QueryInterface(This,riid,ppvObject) ) 

#define Recordset20_AddRef(This)	\
    ( (This)->lpVtbl -> AddRef(This) ) 

#define Recordset20_Release(This)	\
    ( (This)->lpVtbl -> Release(This) ) 


#define Recordset20_GetTypeInfoCount(This,pctinfo)	\
    ( (This)->lpVtbl -> GetTypeInfoCount(This,pctinfo) ) 

#define Recordset20_GetTypeInfo(This,iTInfo,lcid,ppTInfo)	\
    ( (This)->lpVtbl -> GetTypeInfo(This,iTInfo,lcid,ppTInfo) ) 

#define Recordset20_GetIDsOfNames(This,riid,rgszNames,cNames,lcid,rgDispId)	\
    ( (This)->lpVtbl -> GetIDsOfNames(This,riid,rgszNames,cNames,lcid,rgDispId) ) 

#define Recordset20_Invoke(This,dispIdMember,riid,lcid,wFlags,pDispParams,pVarResult,pExcepInfo,puArgErr)	\
    ( (This)->lpVtbl -> Invoke(This,dispIdMember,riid,lcid,wFlags,pDispParams,pVarResult,pExcepInfo,puArgErr) ) 


#define Recordset20_get_Properties(This,ppvObject)	\
    ( (This)->lpVtbl -> get_Properties(This,ppvObject) ) 


#define Recordset20_get_AbsolutePosition(This,pl)	\
    ( (This)->lpVtbl -> get_AbsolutePosition(This,pl) ) 

#define Recordset20_put_AbsolutePosition(This,pl)	\
    ( (This)->lpVtbl -> put_AbsolutePosition(This,pl) ) 

#define Recordset20_putref_ActiveConnection(This,pvar)	\
    ( (This)->lpVtbl -> putref_ActiveConnection(This,pvar) ) 

#define Recordset20_put_ActiveConnection(This,pvar)	\
    ( (This)->lpVtbl -> put_ActiveConnection(This,pvar) ) 

#define Recordset20_get_ActiveConnection(This,pvar)	\
    ( (This)->lpVtbl -> get_ActiveConnection(This,pvar) ) 

#define Recordset20_get_BOF(This,pb)	\
    ( (This)->lpVtbl -> get_BOF(This,pb) ) 

#define Recordset20_get_Bookmark(This,pvBookmark)	\
    ( (This)->lpVtbl -> get_Bookmark(This,pvBookmark) ) 

#define Recordset20_put_Bookmark(This,pvBookmark)	\
    ( (This)->lpVtbl -> put_Bookmark(This,pvBookmark) ) 

#define Recordset20_get_CacheSize(This,pl)	\
    ( (This)->lpVtbl -> get_CacheSize(This,pl) ) 

#define Recordset20_put_CacheSize(This,pl)	\
    ( (This)->lpVtbl -> put_CacheSize(This,pl) ) 

#define Recordset20_get_CursorType(This,plCursorType)	\
    ( (This)->lpVtbl -> get_CursorType(This,plCursorType) ) 

#define Recordset20_put_CursorType(This,plCursorType)	\
    ( (This)->lpVtbl -> put_CursorType(This,plCursorType) ) 

#define Recordset20_get_EOF(This,pb)	\
    ( (This)->lpVtbl -> get_EOF(This,pb) ) 

#define Recordset20_get_Fields(This,ppvObject)	\
    ( (This)->lpVtbl -> get_Fields(This,ppvObject) ) 

#define Recordset20_get_LockType(This,plLockType)	\
    ( (This)->lpVtbl -> get_LockType(This,plLockType) ) 

#define Recordset20_put_LockType(This,plLockType)	\
    ( (This)->lpVtbl -> put_LockType(This,plLockType) ) 

#define Recordset20_get_MaxRecords(This,plMaxRecords)	\
    ( (This)->lpVtbl -> get_MaxRecords(This,plMaxRecords) ) 

#define Recordset20_put_MaxRecords(This,plMaxRecords)	\
    ( (This)->lpVtbl -> put_MaxRecords(This,plMaxRecords) ) 

#define Recordset20_get_RecordCount(This,pl)	\
    ( (This)->lpVtbl -> get_RecordCount(This,pl) ) 

#define Recordset20_putref_Source(This,pvSource)	\
    ( (This)->lpVtbl -> putref_Source(This,pvSource) ) 

#define Recordset20_put_Source(This,pvSource)	\
    ( (This)->lpVtbl -> put_Source(This,pvSource) ) 

#define Recordset20_get_Source(This,pvSource)	\
    ( (This)->lpVtbl -> get_Source(This,pvSource) ) 

#define Recordset20_AddNew(This,FieldList,Values)	\
    ( (This)->lpVtbl -> AddNew(This,FieldList,Values) ) 

#define Recordset20_CancelUpdate(This)	\
    ( (This)->lpVtbl -> CancelUpdate(This) ) 

#define Recordset20_Close(This)	\
    ( (This)->lpVtbl -> Close(This) ) 

#define Recordset20_Delete(This,AffectRecords)	\
    ( (This)->lpVtbl -> Delete(This,AffectRecords) ) 

#define Recordset20_GetRows(This,Rows,Start,Fields,pvar)	\
    ( (This)->lpVtbl -> GetRows(This,Rows,Start,Fields,pvar) ) 

#define Recordset20_Move(This,NumRecords,Start)	\
    ( (This)->lpVtbl -> Move(This,NumRecords,Start) ) 

#define Recordset20_MoveNext(This)	\
    ( (This)->lpVtbl -> MoveNext(This) ) 

#define Recordset20_MovePrevious(This)	\
    ( (This)->lpVtbl -> MovePrevious(This) ) 

#define Recordset20_MoveFirst(This)	\
    ( (This)->lpVtbl -> MoveFirst(This) ) 

#define Recordset20_MoveLast(This)	\
    ( (This)->lpVtbl -> MoveLast(This) ) 

#define Recordset20_Open(This,Source,ActiveConnection,CursorType,LockType,Options)	\
    ( (This)->lpVtbl -> Open(This,Source,ActiveConnection,CursorType,LockType,Options) ) 

#define Recordset20_Requery(This,Options)	\
    ( (This)->lpVtbl -> Requery(This,Options) ) 

#define Recordset20__xResync(This,AffectRecords)	\
    ( (This)->lpVtbl -> _xResync(This,AffectRecords) ) 

#define Recordset20_Update(This,Fields,Values)	\
    ( (This)->lpVtbl -> Update(This,Fields,Values) ) 

#define Recordset20_get_AbsolutePage(This,pl)	\
    ( (This)->lpVtbl -> get_AbsolutePage(This,pl) ) 

#define Recordset20_put_AbsolutePage(This,pl)	\
    ( (This)->lpVtbl -> put_AbsolutePage(This,pl) ) 

#define Recordset20_get_EditMode(This,pl)	\
    ( (This)->lpVtbl -> get_EditMode(This,pl) ) 

#define Recordset20_get_Filter(This,Criteria)	\
    ( (This)->lpVtbl -> get_Filter(This,Criteria) ) 

#define Recordset20_put_Filter(This,Criteria)	\
    ( (This)->lpVtbl -> put_Filter(This,Criteria) ) 

#define Recordset20_get_PageCount(This,pl)	\
    ( (This)->lpVtbl -> get_PageCount(This,pl) ) 

#define Recordset20_get_PageSize(This,pl)	\
    ( (This)->lpVtbl -> get_PageSize(This,pl) ) 

#define Recordset20_put_PageSize(This,pl)	\
    ( (This)->lpVtbl -> put_PageSize(This,pl) ) 

#define Recordset20_get_Sort(This,Criteria)	\
    ( (This)->lpVtbl -> get_Sort(This,Criteria) ) 

#define Recordset20_put_Sort(This,Criteria)	\
    ( (This)->lpVtbl -> put_Sort(This,Criteria) ) 

#define Recordset20_get_Status(This,pl)	\
    ( (This)->lpVtbl -> get_Status(This,pl) ) 

#define Recordset20_get_State(This,plObjState)	\
    ( (This)->lpVtbl -> get_State(This,plObjState) ) 

#define Recordset20__xClone(This,ppvObject)	\
    ( (This)->lpVtbl -> _xClone(This,ppvObject) ) 

#define Recordset20_UpdateBatch(This,AffectRecords)	\
    ( (This)->lpVtbl -> UpdateBatch(This,AffectRecords) ) 

#define Recordset20_CancelBatch(This,AffectRecords)	\
    ( (This)->lpVtbl -> CancelBatch(This,AffectRecords) ) 

#define Recordset20_get_CursorLocation(This,plCursorLoc)	\
    ( (This)->lpVtbl -> get_CursorLocation(This,plCursorLoc) ) 

#define Recordset20_put_CursorLocation(This,plCursorLoc)	\
    ( (This)->lpVtbl -> put_CursorLocation(This,plCursorLoc) ) 

#define Recordset20_NextRecordset(This,RecordsAffected,ppiRs)	\
    ( (This)->lpVtbl -> NextRecordset(This,RecordsAffected,ppiRs) ) 

#define Recordset20_Supports(This,CursorOptions,pb)	\
    ( (This)->lpVtbl -> Supports(This,CursorOptions,pb) ) 

#define Recordset20_get_Collect(This,Index,pvar)	\
    ( (This)->lpVtbl -> get_Collect(This,Index,pvar) ) 

#define Recordset20_put_Collect(This,Index,pvar)	\
    ( (This)->lpVtbl -> put_Collect(This,Index,pvar) ) 

#define Recordset20_get_MarshalOptions(This,peMarshal)	\
    ( (This)->lpVtbl -> get_MarshalOptions(This,peMarshal) ) 

#define Recordset20_put_MarshalOptions(This,peMarshal)	\
    ( (This)->lpVtbl -> put_MarshalOptions(This,peMarshal) ) 

#define Recordset20_Find(This,Criteria,SkipRecords,SearchDirection,Start)	\
    ( (This)->lpVtbl -> Find(This,Criteria,SkipRecords,SearchDirection,Start) ) 


#define Recordset20_Cancel(This)	\
    ( (This)->lpVtbl -> Cancel(This) ) 

#define Recordset20_get_DataSource(This,ppunkDataSource)	\
    ( (This)->lpVtbl -> get_DataSource(This,ppunkDataSource) ) 

#define Recordset20_putref_DataSource(This,ppunkDataSource)	\
    ( (This)->lpVtbl -> putref_DataSource(This,ppunkDataSource) ) 

#define Recordset20__xSave(This,FileName,PersistFormat)	\
    ( (This)->lpVtbl -> _xSave(This,FileName,PersistFormat) ) 

#define Recordset20_get_ActiveCommand(This,ppCmd)	\
    ( (This)->lpVtbl -> get_ActiveCommand(This,ppCmd) ) 

#define Recordset20_put_StayInSync(This,pbStayInSync)	\
    ( (This)->lpVtbl -> put_StayInSync(This,pbStayInSync) ) 

#define Recordset20_get_StayInSync(This,pbStayInSync)	\
    ( (This)->lpVtbl -> get_StayInSync(This,pbStayInSync) ) 

#define Recordset20_GetString(This,StringFormat,NumRows,ColumnDelimeter,RowDelimeter,NullExpr,pRetString)	\
    ( (This)->lpVtbl -> GetString(This,StringFormat,NumRows,ColumnDelimeter,RowDelimeter,NullExpr,pRetString) ) 

#define Recordset20_get_DataMember(This,pbstrDataMember)	\
    ( (This)->lpVtbl -> get_DataMember(This,pbstrDataMember) ) 

#define Recordset20_put_DataMember(This,pbstrDataMember)	\
    ( (This)->lpVtbl -> put_DataMember(This,pbstrDataMember) ) 

#define Recordset20_CompareBookmarks(This,Bookmark1,Bookmark2,pCompare)	\
    ( (This)->lpVtbl -> CompareBookmarks(This,Bookmark1,Bookmark2,pCompare) ) 

#define Recordset20_Clone(This,LockType,ppvObject)	\
    ( (This)->lpVtbl -> Clone(This,LockType,ppvObject) ) 

#define Recordset20_Resync(This,AffectRecords,ResyncValues)	\
    ( (This)->lpVtbl -> Resync(This,AffectRecords,ResyncValues) ) 

#endif /* COBJMACROS */


#endif 	/* C style interface */




#endif 	/* __Recordset20_INTERFACE_DEFINED__ */


#ifndef __Recordset21_INTERFACE_DEFINED__
#define __Recordset21_INTERFACE_DEFINED__

/* interface Recordset21 */
/* [object][oleautomation][nonextensible][dual][uuid] */ 


EXTERN_C const IID IID_Recordset21;

#if defined(__cplusplus) && !defined(CINTERFACE)
    
    MIDL_INTERFACE("00000555-0000-0010-8000-00AA006D2EA4")
    Recordset21 : public Recordset20
    {
    public:
        virtual /* [helpcontext][id] */ HRESULT __stdcall Seek( 
            /* [in] */ VARIANT KeyValues,
            /* [defaultvalue][in] */ SeekEnum SeekOption = adSeekFirstEQ) = 0;
        
        virtual /* [helpcontext][propput][id] */ HRESULT __stdcall put_Index( 
            /* [in] */ BSTR pbstrIndex) = 0;
        
        virtual /* [helpcontext][propget][id] */ HRESULT __stdcall get_Index( 
            /* [retval][out] */ BSTR *pbstrIndex) = 0;
        
    };
    
#else 	/* C style interface */

    typedef struct Recordset21Vtbl
    {
        BEGIN_INTERFACE
        
        HRESULT ( STDMETHODCALLTYPE *QueryInterface )( 
            Recordset21 * This,
            /* [in] */ REFIID riid,
            /* [annotation][iid_is][out] */ 
            __RPC__deref_out  void **ppvObject);
        
        ULONG ( STDMETHODCALLTYPE *AddRef )( 
            Recordset21 * This);
        
        ULONG ( STDMETHODCALLTYPE *Release )( 
            Recordset21 * This);
        
        HRESULT ( STDMETHODCALLTYPE *GetTypeInfoCount )( 
            Recordset21 * This,
            /* [out] */ UINT *pctinfo);
        
        HRESULT ( STDMETHODCALLTYPE *GetTypeInfo )( 
            Recordset21 * This,
            /* [in] */ UINT iTInfo,
            /* [in] */ LCID lcid,
            /* [out] */ ITypeInfo **ppTInfo);
        
        HRESULT ( STDMETHODCALLTYPE *GetIDsOfNames )( 
            Recordset21 * This,
            /* [in] */ REFIID riid,
            /* [size_is][in] */ LPOLESTR *rgszNames,
            /* [range][in] */ UINT cNames,
            /* [in] */ LCID lcid,
            /* [size_is][out] */ DISPID *rgDispId);
        
        /* [local] */ HRESULT ( STDMETHODCALLTYPE *Invoke )( 
            Recordset21 * This,
            /* [in] */ DISPID dispIdMember,
            /* [in] */ REFIID riid,
            /* [in] */ LCID lcid,
            /* [in] */ WORD wFlags,
            /* [out][in] */ DISPPARAMS *pDispParams,
            /* [out] */ VARIANT *pVarResult,
            /* [out] */ EXCEPINFO *pExcepInfo,
            /* [out] */ UINT *puArgErr);
        
        /* [propget][id] */ HRESULT ( __stdcall *get_Properties )( 
            Recordset21 * This,
            /* [retval][out] */ Properties **ppvObject);
        
        /* [propget][id] */ HRESULT ( __stdcall *get_AbsolutePosition )( 
            Recordset21 * This,
            /* [retval][out] */ PositionEnum_Param *pl);
        
        /* [propput][id] */ HRESULT ( __stdcall *put_AbsolutePosition )( 
            Recordset21 * This,
            /* [in] */ PositionEnum_Param pl);
        
        /* [propputref][id] */ HRESULT ( __stdcall *putref_ActiveConnection )( 
            Recordset21 * This,
            /* [in] */ IDispatch *pvar);
        
        /* [propput][id] */ HRESULT ( __stdcall *put_ActiveConnection )( 
            Recordset21 * This,
            /* [in] */ VARIANT pvar);
        
        /* [propget][id] */ HRESULT ( __stdcall *get_ActiveConnection )( 
            Recordset21 * This,
            /* [retval][out] */ VARIANT *pvar);
        
        /* [propget][id] */ HRESULT ( __stdcall *get_BOF )( 
            Recordset21 * This,
            /* [retval][out] */ VARIANT_BOOL *pb);
        
        /* [propget][id] */ HRESULT ( __stdcall *get_Bookmark )( 
            Recordset21 * This,
            /* [retval][out] */ VARIANT *pvBookmark);
        
        /* [propput][id] */ HRESULT ( __stdcall *put_Bookmark )( 
            Recordset21 * This,
            /* [in] */ VARIANT pvBookmark);
        
        /* [propget][id] */ HRESULT ( __stdcall *get_CacheSize )( 
            Recordset21 * This,
            /* [retval][out] */ long *pl);
        
        /* [propput][id] */ HRESULT ( __stdcall *put_CacheSize )( 
            Recordset21 * This,
            /* [in] */ long pl);
        
        /* [propget][id] */ HRESULT ( __stdcall *get_CursorType )( 
            Recordset21 * This,
            /* [retval][out] */ CursorTypeEnum *plCursorType);
        
        /* [propput][id] */ HRESULT ( __stdcall *put_CursorType )( 
            Recordset21 * This,
            /* [in] */ CursorTypeEnum plCursorType);
        
        /* [propget][id] */ HRESULT ( __stdcall *get_EOF )( 
            Recordset21 * This,
            /* [retval][out] */ VARIANT_BOOL *pb);
        
        /* [propget][id] */ HRESULT ( __stdcall *get_Fields )( 
            Recordset21 * This,
            /* [retval][out] */ Fields **ppvObject);
        
        /* [propget][id] */ HRESULT ( __stdcall *get_LockType )( 
            Recordset21 * This,
            /* [retval][out] */ LockTypeEnum *plLockType);
        
        /* [propput][id] */ HRESULT ( __stdcall *put_LockType )( 
            Recordset21 * This,
            /* [in] */ LockTypeEnum plLockType);
        
        /* [propget][id] */ HRESULT ( __stdcall *get_MaxRecords )( 
            Recordset21 * This,
            /* [retval][out] */ ADO_LONGPTR *plMaxRecords);
        
        /* [propput][id] */ HRESULT ( __stdcall *put_MaxRecords )( 
            Recordset21 * This,
            /* [in] */ ADO_LONGPTR plMaxRecords);
        
        /* [propget][id] */ HRESULT ( __stdcall *get_RecordCount )( 
            Recordset21 * This,
            /* [retval][out] */ ADO_LONGPTR *pl);
        
        /* [propputref][id] */ HRESULT ( __stdcall *putref_Source )( 
            Recordset21 * This,
            /* [in] */ IDispatch *pvSource);
        
        /* [propput][id] */ HRESULT ( __stdcall *put_Source )( 
            Recordset21 * This,
            /* [in] */ BSTR pvSource);
        
        /* [propget][id] */ HRESULT ( __stdcall *get_Source )( 
            Recordset21 * This,
            /* [retval][out] */ VARIANT *pvSource);
        
        /* [id] */ HRESULT ( __stdcall *AddNew )( 
            Recordset21 * This,
            /* [optional][in] */ VARIANT FieldList,
            /* [optional][in] */ VARIANT Values);
        
        /* [id] */ HRESULT ( __stdcall *CancelUpdate )( 
            Recordset21 * This);
        
        /* [id] */ HRESULT ( __stdcall *Close )( 
            Recordset21 * This);
        
        /* [id] */ HRESULT ( __stdcall *Delete )( 
            Recordset21 * This,
            /* [defaultvalue][in] */ AffectEnum AffectRecords);
        
        /* [id] */ HRESULT ( __stdcall *GetRows )( 
            Recordset21 * This,
            /* [defaultvalue][in] */ long Rows,
            /* [optional][in] */ VARIANT Start,
            /* [optional][in] */ VARIANT Fields,
            /* [retval][out] */ VARIANT *pvar);
        
        /* [id] */ HRESULT ( __stdcall *Move )( 
            Recordset21 * This,
            /* [in] */ ADO_LONGPTR NumRecords,
            /* [optional][in] */ VARIANT Start);
        
        /* [id] */ HRESULT ( __stdcall *MoveNext )( 
            Recordset21 * This);
        
        /* [id] */ HRESULT ( __stdcall *MovePrevious )( 
            Recordset21 * This);
        
        /* [id] */ HRESULT ( __stdcall *MoveFirst )( 
            Recordset21 * This);
        
        /* [id] */ HRESULT ( __stdcall *MoveLast )( 
            Recordset21 * This);
        
        /* [id] */ HRESULT ( __stdcall *Open )( 
            Recordset21 * This,
            /* [optional][in] */ VARIANT Source,
            /* [optional][in] */ VARIANT ActiveConnection,
            /* [defaultvalue][in] */ CursorTypeEnum CursorType,
            /* [defaultvalue][in] */ LockTypeEnum LockType,
            /* [defaultvalue][in] */ long Options);
        
        /* [id] */ HRESULT ( __stdcall *Requery )( 
            Recordset21 * This,
            /* [defaultvalue][in] */ long Options);
        
        /* [hidden][id] */ HRESULT ( __stdcall *_xResync )( 
            Recordset21 * This,
            /* [defaultvalue][in] */ AffectEnum AffectRecords);
        
        /* [id] */ HRESULT ( __stdcall *Update )( 
            Recordset21 * This,
            /* [optional][in] */ VARIANT Fields,
            /* [optional][in] */ VARIANT Values);
        
        /* [propget][id] */ HRESULT ( __stdcall *get_AbsolutePage )( 
            Recordset21 * This,
            /* [retval][out] */ PositionEnum_Param *pl);
        
        /* [propput][id] */ HRESULT ( __stdcall *put_AbsolutePage )( 
            Recordset21 * This,
            /* [in] */ PositionEnum_Param pl);
        
        /* [propget][id] */ HRESULT ( __stdcall *get_EditMode )( 
            Recordset21 * This,
            /* [retval][out] */ EditModeEnum *pl);
        
        /* [propget][id] */ HRESULT ( __stdcall *get_Filter )( 
            Recordset21 * This,
            /* [retval][out] */ VARIANT *Criteria);
        
        /* [propput][id] */ HRESULT ( __stdcall *put_Filter )( 
            Recordset21 * This,
            /* [in] */ VARIANT Criteria);
        
        /* [propget][id] */ HRESULT ( __stdcall *get_PageCount )( 
            Recordset21 * This,
            /* [retval][out] */ ADO_LONGPTR *pl);
        
        /* [propget][id] */ HRESULT ( __stdcall *get_PageSize )( 
            Recordset21 * This,
            /* [retval][out] */ long *pl);
        
        /* [propput][id] */ HRESULT ( __stdcall *put_PageSize )( 
            Recordset21 * This,
            /* [in] */ long pl);
        
        /* [propget][id] */ HRESULT ( __stdcall *get_Sort )( 
            Recordset21 * This,
            /* [retval][out] */ BSTR *Criteria);
        
        /* [propput][id] */ HRESULT ( __stdcall *put_Sort )( 
            Recordset21 * This,
            /* [in] */ BSTR Criteria);
        
        /* [propget][id] */ HRESULT ( __stdcall *get_Status )( 
            Recordset21 * This,
            /* [retval][out] */ long *pl);
        
        /* [propget][id] */ HRESULT ( __stdcall *get_State )( 
            Recordset21 * This,
            /* [retval][out] */ long *plObjState);
        
        /* [hidden][id] */ HRESULT ( __stdcall *_xClone )( 
            Recordset21 * This,
            /* [retval][out] */ _Recordset **ppvObject);
        
        /* [id] */ HRESULT ( __stdcall *UpdateBatch )( 
            Recordset21 * This,
            /* [defaultvalue][in] */ AffectEnum AffectRecords);
        
        /* [id] */ HRESULT ( __stdcall *CancelBatch )( 
            Recordset21 * This,
            /* [defaultvalue][in] */ AffectEnum AffectRecords);
        
        /* [propget][id] */ HRESULT ( __stdcall *get_CursorLocation )( 
            Recordset21 * This,
            /* [retval][out] */ CursorLocationEnum *plCursorLoc);
        
        /* [propput][id] */ HRESULT ( __stdcall *put_CursorLocation )( 
            Recordset21 * This,
            /* [in] */ CursorLocationEnum plCursorLoc);
        
        /* [id] */ HRESULT ( __stdcall *NextRecordset )( 
            Recordset21 * This,
            /* [optional][out] */ VARIANT *RecordsAffected,
            /* [retval][out] */ _Recordset **ppiRs);
        
        /* [id] */ HRESULT ( __stdcall *Supports )( 
            Recordset21 * This,
            /* [in] */ CursorOptionEnum CursorOptions,
            /* [retval][out] */ VARIANT_BOOL *pb);
        
        /* [hidden][propget][id] */ HRESULT ( __stdcall *get_Collect )( 
            Recordset21 * This,
            /* [in] */ VARIANT Index,
            /* [retval][out] */ VARIANT *pvar);
        
        /* [hidden][propput][id] */ HRESULT ( __stdcall *put_Collect )( 
            Recordset21 * This,
            /* [in] */ VARIANT Index,
            /* [in] */ VARIANT pvar);
        
        /* [propget][id] */ HRESULT ( __stdcall *get_MarshalOptions )( 
            Recordset21 * This,
            /* [retval][out] */ MarshalOptionsEnum *peMarshal);
        
        /* [propput][id] */ HRESULT ( __stdcall *put_MarshalOptions )( 
            Recordset21 * This,
            /* [in] */ MarshalOptionsEnum peMarshal);
        
        /* [id] */ HRESULT ( __stdcall *Find )( 
            Recordset21 * This,
            /* [in] */ BSTR Criteria,
            /* [defaultvalue][in] */ ADO_LONGPTR SkipRecords,
            /* [defaultvalue][in] */ SearchDirectionEnum SearchDirection,
            /* [optional][in] */ VARIANT Start);
        
        /* [id] */ HRESULT ( __stdcall *Cancel )( 
            Recordset21 * This);
        
        /* [propget][id] */ HRESULT ( __stdcall *get_DataSource )( 
            Recordset21 * This,
            /* [retval][out] */ IUnknown **ppunkDataSource);
        
        /* [propputref][id] */ HRESULT ( __stdcall *putref_DataSource )( 
            Recordset21 * This,
            /* [in] */ IUnknown *ppunkDataSource);
        
        /* [hidden] */ HRESULT ( __stdcall *_xSave )( 
            Recordset21 * This,
            /* [optional][in] */ BSTR FileName,
            /* [defaultvalue][in] */ PersistFormatEnum PersistFormat);
        
        /* [propget][id] */ HRESULT ( __stdcall *get_ActiveCommand )( 
            Recordset21 * This,
            /* [retval][out] */ IDispatch **ppCmd);
        
        /* [propput][id] */ HRESULT ( __stdcall *put_StayInSync )( 
            Recordset21 * This,
            /* [in] */ VARIANT_BOOL pbStayInSync);
        
        /* [propget][id] */ HRESULT ( __stdcall *get_StayInSync )( 
            Recordset21 * This,
            /* [retval][out] */ VARIANT_BOOL *pbStayInSync);
        
        /* [id] */ HRESULT ( __stdcall *GetString )( 
            Recordset21 * This,
            /* [defaultvalue][in] */ StringFormatEnum StringFormat,
            /* [defaultvalue][in] */ long NumRows,
            /* [optional][in] */ BSTR ColumnDelimeter,
            /* [optional][in] */ BSTR RowDelimeter,
            /* [optional][in] */ BSTR NullExpr,
            /* [retval][out] */ BSTR *pRetString);
        
        /* [propget][id] */ HRESULT ( __stdcall *get_DataMember )( 
            Recordset21 * This,
            /* [retval][out] */ BSTR *pbstrDataMember);
        
        /* [propput][id] */ HRESULT ( __stdcall *put_DataMember )( 
            Recordset21 * This,
            /* [in] */ BSTR pbstrDataMember);
        
        /* [id] */ HRESULT ( __stdcall *CompareBookmarks )( 
            Recordset21 * This,
            /* [in] */ VARIANT Bookmark1,
            /* [in] */ VARIANT Bookmark2,
            /* [retval][out] */ CompareEnum *pCompare);
        
        /* [id] */ HRESULT ( __stdcall *Clone )( 
            Recordset21 * This,
            /* [defaultvalue][in] */ LockTypeEnum LockType,
            /* [retval][out] */ _Recordset **ppvObject);
        
        /* [id] */ HRESULT ( __stdcall *Resync )( 
            Recordset21 * This,
            /* [defaultvalue][in] */ AffectEnum AffectRecords,
            /* [defaultvalue][in] */ ResyncEnum ResyncValues);
        
        /* [helpcontext][id] */ HRESULT ( __stdcall *Seek )( 
            Recordset21 * This,
            /* [in] */ VARIANT KeyValues,
            /* [defaultvalue][in] */ SeekEnum SeekOption);
        
        /* [helpcontext][propput][id] */ HRESULT ( __stdcall *put_Index )( 
            Recordset21 * This,
            /* [in] */ BSTR pbstrIndex);
        
        /* [helpcontext][propget][id] */ HRESULT ( __stdcall *get_Index )( 
            Recordset21 * This,
            /* [retval][out] */ BSTR *pbstrIndex);
        
        END_INTERFACE
    } Recordset21Vtbl;

    interface Recordset21
    {
        CONST_VTBL struct Recordset21Vtbl *lpVtbl;
    };

    

#ifdef COBJMACROS


#define Recordset21_QueryInterface(This,riid,ppvObject)	\
    ( (This)->lpVtbl -> QueryInterface(This,riid,ppvObject) ) 

#define Recordset21_AddRef(This)	\
    ( (This)->lpVtbl -> AddRef(This) ) 

#define Recordset21_Release(This)	\
    ( (This)->lpVtbl -> Release(This) ) 


#define Recordset21_GetTypeInfoCount(This,pctinfo)	\
    ( (This)->lpVtbl -> GetTypeInfoCount(This,pctinfo) ) 

#define Recordset21_GetTypeInfo(This,iTInfo,lcid,ppTInfo)	\
    ( (This)->lpVtbl -> GetTypeInfo(This,iTInfo,lcid,ppTInfo) ) 

#define Recordset21_GetIDsOfNames(This,riid,rgszNames,cNames,lcid,rgDispId)	\
    ( (This)->lpVtbl -> GetIDsOfNames(This,riid,rgszNames,cNames,lcid,rgDispId) ) 

#define Recordset21_Invoke(This,dispIdMember,riid,lcid,wFlags,pDispParams,pVarResult,pExcepInfo,puArgErr)	\
    ( (This)->lpVtbl -> Invoke(This,dispIdMember,riid,lcid,wFlags,pDispParams,pVarResult,pExcepInfo,puArgErr) ) 


#define Recordset21_get_Properties(This,ppvObject)	\
    ( (This)->lpVtbl -> get_Properties(This,ppvObject) ) 


#define Recordset21_get_AbsolutePosition(This,pl)	\
    ( (This)->lpVtbl -> get_AbsolutePosition(This,pl) ) 

#define Recordset21_put_AbsolutePosition(This,pl)	\
    ( (This)->lpVtbl -> put_AbsolutePosition(This,pl) ) 

#define Recordset21_putref_ActiveConnection(This,pvar)	\
    ( (This)->lpVtbl -> putref_ActiveConnection(This,pvar) ) 

#define Recordset21_put_ActiveConnection(This,pvar)	\
    ( (This)->lpVtbl -> put_ActiveConnection(This,pvar) ) 

#define Recordset21_get_ActiveConnection(This,pvar)	\
    ( (This)->lpVtbl -> get_ActiveConnection(This,pvar) ) 

#define Recordset21_get_BOF(This,pb)	\
    ( (This)->lpVtbl -> get_BOF(This,pb) ) 

#define Recordset21_get_Bookmark(This,pvBookmark)	\
    ( (This)->lpVtbl -> get_Bookmark(This,pvBookmark) ) 

#define Recordset21_put_Bookmark(This,pvBookmark)	\
    ( (This)->lpVtbl -> put_Bookmark(This,pvBookmark) ) 

#define Recordset21_get_CacheSize(This,pl)	\
    ( (This)->lpVtbl -> get_CacheSize(This,pl) ) 

#define Recordset21_put_CacheSize(This,pl)	\
    ( (This)->lpVtbl -> put_CacheSize(This,pl) ) 

#define Recordset21_get_CursorType(This,plCursorType)	\
    ( (This)->lpVtbl -> get_CursorType(This,plCursorType) ) 

#define Recordset21_put_CursorType(This,plCursorType)	\
    ( (This)->lpVtbl -> put_CursorType(This,plCursorType) ) 

#define Recordset21_get_EOF(This,pb)	\
    ( (This)->lpVtbl -> get_EOF(This,pb) ) 

#define Recordset21_get_Fields(This,ppvObject)	\
    ( (This)->lpVtbl -> get_Fields(This,ppvObject) ) 

#define Recordset21_get_LockType(This,plLockType)	\
    ( (This)->lpVtbl -> get_LockType(This,plLockType) ) 

#define Recordset21_put_LockType(This,plLockType)	\
    ( (This)->lpVtbl -> put_LockType(This,plLockType) ) 

#define Recordset21_get_MaxRecords(This,plMaxRecords)	\
    ( (This)->lpVtbl -> get_MaxRecords(This,plMaxRecords) ) 

#define Recordset21_put_MaxRecords(This,plMaxRecords)	\
    ( (This)->lpVtbl -> put_MaxRecords(This,plMaxRecords) ) 

#define Recordset21_get_RecordCount(This,pl)	\
    ( (This)->lpVtbl -> get_RecordCount(This,pl) ) 

#define Recordset21_putref_Source(This,pvSource)	\
    ( (This)->lpVtbl -> putref_Source(This,pvSource) ) 

#define Recordset21_put_Source(This,pvSource)	\
    ( (This)->lpVtbl -> put_Source(This,pvSource) ) 

#define Recordset21_get_Source(This,pvSource)	\
    ( (This)->lpVtbl -> get_Source(This,pvSource) ) 

#define Recordset21_AddNew(This,FieldList,Values)	\
    ( (This)->lpVtbl -> AddNew(This,FieldList,Values) ) 

#define Recordset21_CancelUpdate(This)	\
    ( (This)->lpVtbl -> CancelUpdate(This) ) 

#define Recordset21_Close(This)	\
    ( (This)->lpVtbl -> Close(This) ) 

#define Recordset21_Delete(This,AffectRecords)	\
    ( (This)->lpVtbl -> Delete(This,AffectRecords) ) 

#define Recordset21_GetRows(This,Rows,Start,Fields,pvar)	\
    ( (This)->lpVtbl -> GetRows(This,Rows,Start,Fields,pvar) ) 

#define Recordset21_Move(This,NumRecords,Start)	\
    ( (This)->lpVtbl -> Move(This,NumRecords,Start) ) 

#define Recordset21_MoveNext(This)	\
    ( (This)->lpVtbl -> MoveNext(This) ) 

#define Recordset21_MovePrevious(This)	\
    ( (This)->lpVtbl -> MovePrevious(This) ) 

#define Recordset21_MoveFirst(This)	\
    ( (This)->lpVtbl -> MoveFirst(This) ) 

#define Recordset21_MoveLast(This)	\
    ( (This)->lpVtbl -> MoveLast(This) ) 

#define Recordset21_Open(This,Source,ActiveConnection,CursorType,LockType,Options)	\
    ( (This)->lpVtbl -> Open(This,Source,ActiveConnection,CursorType,LockType,Options) ) 

#define Recordset21_Requery(This,Options)	\
    ( (This)->lpVtbl -> Requery(This,Options) ) 

#define Recordset21__xResync(This,AffectRecords)	\
    ( (This)->lpVtbl -> _xResync(This,AffectRecords) ) 

#define Recordset21_Update(This,Fields,Values)	\
    ( (This)->lpVtbl -> Update(This,Fields,Values) ) 

#define Recordset21_get_AbsolutePage(This,pl)	\
    ( (This)->lpVtbl -> get_AbsolutePage(This,pl) ) 

#define Recordset21_put_AbsolutePage(This,pl)	\
    ( (This)->lpVtbl -> put_AbsolutePage(This,pl) ) 

#define Recordset21_get_EditMode(This,pl)	\
    ( (This)->lpVtbl -> get_EditMode(This,pl) ) 

#define Recordset21_get_Filter(This,Criteria)	\
    ( (This)->lpVtbl -> get_Filter(This,Criteria) ) 

#define Recordset21_put_Filter(This,Criteria)	\
    ( (This)->lpVtbl -> put_Filter(This,Criteria) ) 

#define Recordset21_get_PageCount(This,pl)	\
    ( (This)->lpVtbl -> get_PageCount(This,pl) ) 

#define Recordset21_get_PageSize(This,pl)	\
    ( (This)->lpVtbl -> get_PageSize(This,pl) ) 

#define Recordset21_put_PageSize(This,pl)	\
    ( (This)->lpVtbl -> put_PageSize(This,pl) ) 

#define Recordset21_get_Sort(This,Criteria)	\
    ( (This)->lpVtbl -> get_Sort(This,Criteria) ) 

#define Recordset21_put_Sort(This,Criteria)	\
    ( (This)->lpVtbl -> put_Sort(This,Criteria) ) 

#define Recordset21_get_Status(This,pl)	\
    ( (This)->lpVtbl -> get_Status(This,pl) ) 

#define Recordset21_get_State(This,plObjState)	\
    ( (This)->lpVtbl -> get_State(This,plObjState) ) 

#define Recordset21__xClone(This,ppvObject)	\
    ( (This)->lpVtbl -> _xClone(This,ppvObject) ) 

#define Recordset21_UpdateBatch(This,AffectRecords)	\
    ( (This)->lpVtbl -> UpdateBatch(This,AffectRecords) ) 

#define Recordset21_CancelBatch(This,AffectRecords)	\
    ( (This)->lpVtbl -> CancelBatch(This,AffectRecords) ) 

#define Recordset21_get_CursorLocation(This,plCursorLoc)	\
    ( (This)->lpVtbl -> get_CursorLocation(This,plCursorLoc) ) 

#define Recordset21_put_CursorLocation(This,plCursorLoc)	\
    ( (This)->lpVtbl -> put_CursorLocation(This,plCursorLoc) ) 

#define Recordset21_NextRecordset(This,RecordsAffected,ppiRs)	\
    ( (This)->lpVtbl -> NextRecordset(This,RecordsAffected,ppiRs) ) 

#define Recordset21_Supports(This,CursorOptions,pb)	\
    ( (This)->lpVtbl -> Supports(This,CursorOptions,pb) ) 

#define Recordset21_get_Collect(This,Index,pvar)	\
    ( (This)->lpVtbl -> get_Collect(This,Index,pvar) ) 

#define Recordset21_put_Collect(This,Index,pvar)	\
    ( (This)->lpVtbl -> put_Collect(This,Index,pvar) ) 

#define Recordset21_get_MarshalOptions(This,peMarshal)	\
    ( (This)->lpVtbl -> get_MarshalOptions(This,peMarshal) ) 

#define Recordset21_put_MarshalOptions(This,peMarshal)	\
    ( (This)->lpVtbl -> put_MarshalOptions(This,peMarshal) ) 

#define Recordset21_Find(This,Criteria,SkipRecords,SearchDirection,Start)	\
    ( (This)->lpVtbl -> Find(This,Criteria,SkipRecords,SearchDirection,Start) ) 


#define Recordset21_Cancel(This)	\
    ( (This)->lpVtbl -> Cancel(This) ) 

#define Recordset21_get_DataSource(This,ppunkDataSource)	\
    ( (This)->lpVtbl -> get_DataSource(This,ppunkDataSource) ) 

#define Recordset21_putref_DataSource(This,ppunkDataSource)	\
    ( (This)->lpVtbl -> putref_DataSource(This,ppunkDataSource) ) 

#define Recordset21__xSave(This,FileName,PersistFormat)	\
    ( (This)->lpVtbl -> _xSave(This,FileName,PersistFormat) ) 

#define Recordset21_get_ActiveCommand(This,ppCmd)	\
    ( (This)->lpVtbl -> get_ActiveCommand(This,ppCmd) ) 

#define Recordset21_put_StayInSync(This,pbStayInSync)	\
    ( (This)->lpVtbl -> put_StayInSync(This,pbStayInSync) ) 

#define Recordset21_get_StayInSync(This,pbStayInSync)	\
    ( (This)->lpVtbl -> get_StayInSync(This,pbStayInSync) ) 

#define Recordset21_GetString(This,StringFormat,NumRows,ColumnDelimeter,RowDelimeter,NullExpr,pRetString)	\
    ( (This)->lpVtbl -> GetString(This,StringFormat,NumRows,ColumnDelimeter,RowDelimeter,NullExpr,pRetString) ) 

#define Recordset21_get_DataMember(This,pbstrDataMember)	\
    ( (This)->lpVtbl -> get_DataMember(This,pbstrDataMember) ) 

#define Recordset21_put_DataMember(This,pbstrDataMember)	\
    ( (This)->lpVtbl -> put_DataMember(This,pbstrDataMember) ) 

#define Recordset21_CompareBookmarks(This,Bookmark1,Bookmark2,pCompare)	\
    ( (This)->lpVtbl -> CompareBookmarks(This,Bookmark1,Bookmark2,pCompare) ) 

#define Recordset21_Clone(This,LockType,ppvObject)	\
    ( (This)->lpVtbl -> Clone(This,LockType,ppvObject) ) 

#define Recordset21_Resync(This,AffectRecords,ResyncValues)	\
    ( (This)->lpVtbl -> Resync(This,AffectRecords,ResyncValues) ) 


#define Recordset21_Seek(This,KeyValues,SeekOption)	\
    ( (This)->lpVtbl -> Seek(This,KeyValues,SeekOption) ) 

#define Recordset21_put_Index(This,pbstrIndex)	\
    ( (This)->lpVtbl -> put_Index(This,pbstrIndex) ) 

#define Recordset21_get_Index(This,pbstrIndex)	\
    ( (This)->lpVtbl -> get_Index(This,pbstrIndex) ) 

#endif /* COBJMACROS */


#endif 	/* C style interface */




#endif 	/* __Recordset21_INTERFACE_DEFINED__ */


#ifndef ___Recordset_INTERFACE_DEFINED__
#define ___Recordset_INTERFACE_DEFINED__

/* interface _Recordset */
/* [object][oleautomation][nonextensible][dual][uuid] */ 


EXTERN_C const IID IID__Recordset;

#if defined(__cplusplus) && !defined(CINTERFACE)
    
    MIDL_INTERFACE("00000556-0000-0010-8000-00AA006D2EA4")
    _Recordset : public Recordset21
    {
    public:
        virtual /* [helpcontext][id] */ HRESULT STDMETHODCALLTYPE Save( 
            /* [optional][in] */ VARIANT Destination,
            /* [defaultvalue][in] */ PersistFormatEnum PersistFormat = adPersistADTG) = 0;
        
    };
    
#else 	/* C style interface */

    typedef struct _RecordsetVtbl
    {
        BEGIN_INTERFACE
        
        HRESULT ( STDMETHODCALLTYPE *QueryInterface )( 
            _Recordset * This,
            /* [in] */ REFIID riid,
            /* [annotation][iid_is][out] */ 
            __RPC__deref_out  void **ppvObject);
        
        ULONG ( STDMETHODCALLTYPE *AddRef )( 
            _Recordset * This);
        
        ULONG ( STDMETHODCALLTYPE *Release )( 
            _Recordset * This);
        
        HRESULT ( STDMETHODCALLTYPE *GetTypeInfoCount )( 
            _Recordset * This,
            /* [out] */ UINT *pctinfo);
        
        HRESULT ( STDMETHODCALLTYPE *GetTypeInfo )( 
            _Recordset * This,
            /* [in] */ UINT iTInfo,
            /* [in] */ LCID lcid,
            /* [out] */ ITypeInfo **ppTInfo);
        
        HRESULT ( STDMETHODCALLTYPE *GetIDsOfNames )( 
            _Recordset * This,
            /* [in] */ REFIID riid,
            /* [size_is][in] */ LPOLESTR *rgszNames,
            /* [range][in] */ UINT cNames,
            /* [in] */ LCID lcid,
            /* [size_is][out] */ DISPID *rgDispId);
        
        /* [local] */ HRESULT ( STDMETHODCALLTYPE *Invoke )( 
            _Recordset * This,
            /* [in] */ DISPID dispIdMember,
            /* [in] */ REFIID riid,
            /* [in] */ LCID lcid,
            /* [in] */ WORD wFlags,
            /* [out][in] */ DISPPARAMS *pDispParams,
            /* [out] */ VARIANT *pVarResult,
            /* [out] */ EXCEPINFO *pExcepInfo,
            /* [out] */ UINT *puArgErr);
        
        /* [propget][id] */ HRESULT ( __stdcall *get_Properties )( 
            _Recordset * This,
            /* [retval][out] */ Properties **ppvObject);
        
        /* [propget][id] */ HRESULT ( __stdcall *get_AbsolutePosition )( 
            _Recordset * This,
            /* [retval][out] */ PositionEnum_Param *pl);
        
        /* [propput][id] */ HRESULT ( __stdcall *put_AbsolutePosition )( 
            _Recordset * This,
            /* [in] */ PositionEnum_Param pl);
        
        /* [propputref][id] */ HRESULT ( __stdcall *putref_ActiveConnection )( 
            _Recordset * This,
            /* [in] */ IDispatch *pvar);
        
        /* [propput][id] */ HRESULT ( __stdcall *put_ActiveConnection )( 
            _Recordset * This,
            /* [in] */ VARIANT pvar);
        
        /* [propget][id] */ HRESULT ( __stdcall *get_ActiveConnection )( 
            _Recordset * This,
            /* [retval][out] */ VARIANT *pvar);
        
        /* [propget][id] */ HRESULT ( __stdcall *get_BOF )( 
            _Recordset * This,
            /* [retval][out] */ VARIANT_BOOL *pb);
        
        /* [propget][id] */ HRESULT ( __stdcall *get_Bookmark )( 
            _Recordset * This,
            /* [retval][out] */ VARIANT *pvBookmark);
        
        /* [propput][id] */ HRESULT ( __stdcall *put_Bookmark )( 
            _Recordset * This,
            /* [in] */ VARIANT pvBookmark);
        
        /* [propget][id] */ HRESULT ( __stdcall *get_CacheSize )( 
            _Recordset * This,
            /* [retval][out] */ long *pl);
        
        /* [propput][id] */ HRESULT ( __stdcall *put_CacheSize )( 
            _Recordset * This,
            /* [in] */ long pl);
        
        /* [propget][id] */ HRESULT ( __stdcall *get_CursorType )( 
            _Recordset * This,
            /* [retval][out] */ CursorTypeEnum *plCursorType);
        
        /* [propput][id] */ HRESULT ( __stdcall *put_CursorType )( 
            _Recordset * This,
            /* [in] */ CursorTypeEnum plCursorType);
        
        /* [propget][id] */ HRESULT ( __stdcall *get_EOF )( 
            _Recordset * This,
            /* [retval][out] */ VARIANT_BOOL *pb);
        
        /* [propget][id] */ HRESULT ( __stdcall *get_Fields )( 
            _Recordset * This,
            /* [retval][out] */ Fields **ppvObject);
        
        /* [propget][id] */ HRESULT ( __stdcall *get_LockType )( 
            _Recordset * This,
            /* [retval][out] */ LockTypeEnum *plLockType);
        
        /* [propput][id] */ HRESULT ( __stdcall *put_LockType )( 
            _Recordset * This,
            /* [in] */ LockTypeEnum plLockType);
        
        /* [propget][id] */ HRESULT ( __stdcall *get_MaxRecords )( 
            _Recordset * This,
            /* [retval][out] */ ADO_LONGPTR *plMaxRecords);
        
        /* [propput][id] */ HRESULT ( __stdcall *put_MaxRecords )( 
            _Recordset * This,
            /* [in] */ ADO_LONGPTR plMaxRecords);
        
        /* [propget][id] */ HRESULT ( __stdcall *get_RecordCount )( 
            _Recordset * This,
            /* [retval][out] */ ADO_LONGPTR *pl);
        
        /* [propputref][id] */ HRESULT ( __stdcall *putref_Source )( 
            _Recordset * This,
            /* [in] */ IDispatch *pvSource);
        
        /* [propput][id] */ HRESULT ( __stdcall *put_Source )( 
            _Recordset * This,
            /* [in] */ BSTR pvSource);
        
        /* [propget][id] */ HRESULT ( __stdcall *get_Source )( 
            _Recordset * This,
            /* [retval][out] */ VARIANT *pvSource);
        
        /* [id] */ HRESULT ( __stdcall *AddNew )( 
            _Recordset * This,
            /* [optional][in] */ VARIANT FieldList,
            /* [optional][in] */ VARIANT Values);
        
        /* [id] */ HRESULT ( __stdcall *CancelUpdate )( 
            _Recordset * This);
        
        /* [id] */ HRESULT ( __stdcall *Close )( 
            _Recordset * This);
        
        /* [id] */ HRESULT ( __stdcall *Delete )( 
            _Recordset * This,
            /* [defaultvalue][in] */ AffectEnum AffectRecords);
        
        /* [id] */ HRESULT ( __stdcall *GetRows )( 
            _Recordset * This,
            /* [defaultvalue][in] */ long Rows,
            /* [optional][in] */ VARIANT Start,
            /* [optional][in] */ VARIANT Fields,
            /* [retval][out] */ VARIANT *pvar);
        
        /* [id] */ HRESULT ( __stdcall *Move )( 
            _Recordset * This,
            /* [in] */ ADO_LONGPTR NumRecords,
            /* [optional][in] */ VARIANT Start);
        
        /* [id] */ HRESULT ( __stdcall *MoveNext )( 
            _Recordset * This);
        
        /* [id] */ HRESULT ( __stdcall *MovePrevious )( 
            _Recordset * This);
        
        /* [id] */ HRESULT ( __stdcall *MoveFirst )( 
            _Recordset * This);
        
        /* [id] */ HRESULT ( __stdcall *MoveLast )( 
            _Recordset * This);
        
        /* [id] */ HRESULT ( __stdcall *Open )( 
            _Recordset * This,
            /* [optional][in] */ VARIANT Source,
            /* [optional][in] */ VARIANT ActiveConnection,
            /* [defaultvalue][in] */ CursorTypeEnum CursorType,
            /* [defaultvalue][in] */ LockTypeEnum LockType,
            /* [defaultvalue][in] */ long Options);
        
        /* [id] */ HRESULT ( __stdcall *Requery )( 
            _Recordset * This,
            /* [defaultvalue][in] */ long Options);
        
        /* [hidden][id] */ HRESULT ( __stdcall *_xResync )( 
            _Recordset * This,
            /* [defaultvalue][in] */ AffectEnum AffectRecords);
        
        /* [id] */ HRESULT ( __stdcall *Update )( 
            _Recordset * This,
            /* [optional][in] */ VARIANT Fields,
            /* [optional][in] */ VARIANT Values);
        
        /* [propget][id] */ HRESULT ( __stdcall *get_AbsolutePage )( 
            _Recordset * This,
            /* [retval][out] */ PositionEnum_Param *pl);
        
        /* [propput][id] */ HRESULT ( __stdcall *put_AbsolutePage )( 
            _Recordset * This,
            /* [in] */ PositionEnum_Param pl);
        
        /* [propget][id] */ HRESULT ( __stdcall *get_EditMode )( 
            _Recordset * This,
            /* [retval][out] */ EditModeEnum *pl);
        
        /* [propget][id] */ HRESULT ( __stdcall *get_Filter )( 
            _Recordset * This,
            /* [retval][out] */ VARIANT *Criteria);
        
        /* [propput][id] */ HRESULT ( __stdcall *put_Filter )( 
            _Recordset * This,
            /* [in] */ VARIANT Criteria);
        
        /* [propget][id] */ HRESULT ( __stdcall *get_PageCount )( 
            _Recordset * This,
            /* [retval][out] */ ADO_LONGPTR *pl);
        
        /* [propget][id] */ HRESULT ( __stdcall *get_PageSize )( 
            _Recordset * This,
            /* [retval][out] */ long *pl);
        
        /* [propput][id] */ HRESULT ( __stdcall *put_PageSize )( 
            _Recordset * This,
            /* [in] */ long pl);
        
        /* [propget][id] */ HRESULT ( __stdcall *get_Sort )( 
            _Recordset * This,
            /* [retval][out] */ BSTR *Criteria);
        
        /* [propput][id] */ HRESULT ( __stdcall *put_Sort )( 
            _Recordset * This,
            /* [in] */ BSTR Criteria);
        
        /* [propget][id] */ HRESULT ( __stdcall *get_Status )( 
            _Recordset * This,
            /* [retval][out] */ long *pl);
        
        /* [propget][id] */ HRESULT ( __stdcall *get_State )( 
            _Recordset * This,
            /* [retval][out] */ long *plObjState);
        
        /* [hidden][id] */ HRESULT ( __stdcall *_xClone )( 
            _Recordset * This,
            /* [retval][out] */ _Recordset **ppvObject);
        
        /* [id] */ HRESULT ( __stdcall *UpdateBatch )( 
            _Recordset * This,
            /* [defaultvalue][in] */ AffectEnum AffectRecords);
        
        /* [id] */ HRESULT ( __stdcall *CancelBatch )( 
            _Recordset * This,
            /* [defaultvalue][in] */ AffectEnum AffectRecords);
        
        /* [propget][id] */ HRESULT ( __stdcall *get_CursorLocation )( 
            _Recordset * This,
            /* [retval][out] */ CursorLocationEnum *plCursorLoc);
        
        /* [propput][id] */ HRESULT ( __stdcall *put_CursorLocation )( 
            _Recordset * This,
            /* [in] */ CursorLocationEnum plCursorLoc);
        
        /* [id] */ HRESULT ( __stdcall *NextRecordset )( 
            _Recordset * This,
            /* [optional][out] */ VARIANT *RecordsAffected,
            /* [retval][out] */ _Recordset **ppiRs);
        
        /* [id] */ HRESULT ( __stdcall *Supports )( 
            _Recordset * This,
            /* [in] */ CursorOptionEnum CursorOptions,
            /* [retval][out] */ VARIANT_BOOL *pb);
        
        /* [hidden][propget][id] */ HRESULT ( __stdcall *get_Collect )( 
            _Recordset * This,
            /* [in] */ VARIANT Index,
            /* [retval][out] */ VARIANT *pvar);
        
        /* [hidden][propput][id] */ HRESULT ( __stdcall *put_Collect )( 
            _Recordset * This,
            /* [in] */ VARIANT Index,
            /* [in] */ VARIANT pvar);
        
        /* [propget][id] */ HRESULT ( __stdcall *get_MarshalOptions )( 
            _Recordset * This,
            /* [retval][out] */ MarshalOptionsEnum *peMarshal);
        
        /* [propput][id] */ HRESULT ( __stdcall *put_MarshalOptions )( 
            _Recordset * This,
            /* [in] */ MarshalOptionsEnum peMarshal);
        
        /* [id] */ HRESULT ( __stdcall *Find )( 
            _Recordset * This,
            /* [in] */ BSTR Criteria,
            /* [defaultvalue][in] */ ADO_LONGPTR SkipRecords,
            /* [defaultvalue][in] */ SearchDirectionEnum SearchDirection,
            /* [optional][in] */ VARIANT Start);
        
        /* [id] */ HRESULT ( __stdcall *Cancel )( 
            _Recordset * This);
        
        /* [propget][id] */ HRESULT ( __stdcall *get_DataSource )( 
            _Recordset * This,
            /* [retval][out] */ IUnknown **ppunkDataSource);
        
        /* [propputref][id] */ HRESULT ( __stdcall *putref_DataSource )( 
            _Recordset * This,
            /* [in] */ IUnknown *ppunkDataSource);
        
        /* [hidden] */ HRESULT ( __stdcall *_xSave )( 
            _Recordset * This,
            /* [optional][in] */ BSTR FileName,
            /* [defaultvalue][in] */ PersistFormatEnum PersistFormat);
        
        /* [propget][id] */ HRESULT ( __stdcall *get_ActiveCommand )( 
            _Recordset * This,
            /* [retval][out] */ IDispatch **ppCmd);
        
        /* [propput][id] */ HRESULT ( __stdcall *put_StayInSync )( 
            _Recordset * This,
            /* [in] */ VARIANT_BOOL pbStayInSync);
        
        /* [propget][id] */ HRESULT ( __stdcall *get_StayInSync )( 
            _Recordset * This,
            /* [retval][out] */ VARIANT_BOOL *pbStayInSync);
        
        /* [id] */ HRESULT ( __stdcall *GetString )( 
            _Recordset * This,
            /* [defaultvalue][in] */ StringFormatEnum StringFormat,
            /* [defaultvalue][in] */ long NumRows,
            /* [optional][in] */ BSTR ColumnDelimeter,
            /* [optional][in] */ BSTR RowDelimeter,
            /* [optional][in] */ BSTR NullExpr,
            /* [retval][out] */ BSTR *pRetString);
        
        /* [propget][id] */ HRESULT ( __stdcall *get_DataMember )( 
            _Recordset * This,
            /* [retval][out] */ BSTR *pbstrDataMember);
        
        /* [propput][id] */ HRESULT ( __stdcall *put_DataMember )( 
            _Recordset * This,
            /* [in] */ BSTR pbstrDataMember);
        
        /* [id] */ HRESULT ( __stdcall *CompareBookmarks )( 
            _Recordset * This,
            /* [in] */ VARIANT Bookmark1,
            /* [in] */ VARIANT Bookmark2,
            /* [retval][out] */ CompareEnum *pCompare);
        
        /* [id] */ HRESULT ( __stdcall *Clone )( 
            _Recordset * This,
            /* [defaultvalue][in] */ LockTypeEnum LockType,
            /* [retval][out] */ _Recordset **ppvObject);
        
        /* [id] */ HRESULT ( __stdcall *Resync )( 
            _Recordset * This,
            /* [defaultvalue][in] */ AffectEnum AffectRecords,
            /* [defaultvalue][in] */ ResyncEnum ResyncValues);
        
        /* [helpcontext][id] */ HRESULT ( __stdcall *Seek )( 
            _Recordset * This,
            /* [in] */ VARIANT KeyValues,
            /* [defaultvalue][in] */ SeekEnum SeekOption);
        
        /* [helpcontext][propput][id] */ HRESULT ( __stdcall *put_Index )( 
            _Recordset * This,
            /* [in] */ BSTR pbstrIndex);
        
        /* [helpcontext][propget][id] */ HRESULT ( __stdcall *get_Index )( 
            _Recordset * This,
            /* [retval][out] */ BSTR *pbstrIndex);
        
        /* [helpcontext][id] */ HRESULT ( STDMETHODCALLTYPE *Save )( 
            _Recordset * This,
            /* [optional][in] */ VARIANT Destination,
            /* [defaultvalue][in] */ PersistFormatEnum PersistFormat);
        
        END_INTERFACE
    } _RecordsetVtbl;

    interface _Recordset
    {
        CONST_VTBL struct _RecordsetVtbl *lpVtbl;
    };

    

#ifdef COBJMACROS


#define _Recordset_QueryInterface(This,riid,ppvObject)	\
    ( (This)->lpVtbl -> QueryInterface(This,riid,ppvObject) ) 

#define _Recordset_AddRef(This)	\
    ( (This)->lpVtbl -> AddRef(This) ) 

#define _Recordset_Release(This)	\
    ( (This)->lpVtbl -> Release(This) ) 


#define _Recordset_GetTypeInfoCount(This,pctinfo)	\
    ( (This)->lpVtbl -> GetTypeInfoCount(This,pctinfo) ) 

#define _Recordset_GetTypeInfo(This,iTInfo,lcid,ppTInfo)	\
    ( (This)->lpVtbl -> GetTypeInfo(This,iTInfo,lcid,ppTInfo) ) 

#define _Recordset_GetIDsOfNames(This,riid,rgszNames,cNames,lcid,rgDispId)	\
    ( (This)->lpVtbl -> GetIDsOfNames(This,riid,rgszNames,cNames,lcid,rgDispId) ) 

#define _Recordset_Invoke(This,dispIdMember,riid,lcid,wFlags,pDispParams,pVarResult,pExcepInfo,puArgErr)	\
    ( (This)->lpVtbl -> Invoke(This,dispIdMember,riid,lcid,wFlags,pDispParams,pVarResult,pExcepInfo,puArgErr) ) 


#define _Recordset_get_Properties(This,ppvObject)	\
    ( (This)->lpVtbl -> get_Properties(This,ppvObject) ) 


#define _Recordset_get_AbsolutePosition(This,pl)	\
    ( (This)->lpVtbl -> get_AbsolutePosition(This,pl) ) 

#define _Recordset_put_AbsolutePosition(This,pl)	\
    ( (This)->lpVtbl -> put_AbsolutePosition(This,pl) ) 

#define _Recordset_putref_ActiveConnection(This,pvar)	\
    ( (This)->lpVtbl -> putref_ActiveConnection(This,pvar) ) 

#define _Recordset_put_ActiveConnection(This,pvar)	\
    ( (This)->lpVtbl -> put_ActiveConnection(This,pvar) ) 

#define _Recordset_get_ActiveConnection(This,pvar)	\
    ( (This)->lpVtbl -> get_ActiveConnection(This,pvar) ) 

#define _Recordset_get_BOF(This,pb)	\
    ( (This)->lpVtbl -> get_BOF(This,pb) ) 

#define _Recordset_get_Bookmark(This,pvBookmark)	\
    ( (This)->lpVtbl -> get_Bookmark(This,pvBookmark) ) 

#define _Recordset_put_Bookmark(This,pvBookmark)	\
    ( (This)->lpVtbl -> put_Bookmark(This,pvBookmark) ) 

#define _Recordset_get_CacheSize(This,pl)	\
    ( (This)->lpVtbl -> get_CacheSize(This,pl) ) 

#define _Recordset_put_CacheSize(This,pl)	\
    ( (This)->lpVtbl -> put_CacheSize(This,pl) ) 

#define _Recordset_get_CursorType(This,plCursorType)	\
    ( (This)->lpVtbl -> get_CursorType(This,plCursorType) ) 

#define _Recordset_put_CursorType(This,plCursorType)	\
    ( (This)->lpVtbl -> put_CursorType(This,plCursorType) ) 

#define _Recordset_get_EOF(This,pb)	\
    ( (This)->lpVtbl -> get_EOF(This,pb) ) 

#define _Recordset_get_Fields(This,ppvObject)	\
    ( (This)->lpVtbl -> get_Fields(This,ppvObject) ) 

#define _Recordset_get_LockType(This,plLockType)	\
    ( (This)->lpVtbl -> get_LockType(This,plLockType) ) 

#define _Recordset_put_LockType(This,plLockType)	\
    ( (This)->lpVtbl -> put_LockType(This,plLockType) ) 

#define _Recordset_get_MaxRecords(This,plMaxRecords)	\
    ( (This)->lpVtbl -> get_MaxRecords(This,plMaxRecords) ) 

#define _Recordset_put_MaxRecords(This,plMaxRecords)	\
    ( (This)->lpVtbl -> put_MaxRecords(This,plMaxRecords) ) 

#define _Recordset_get_RecordCount(This,pl)	\
    ( (This)->lpVtbl -> get_RecordCount(This,pl) ) 

#define _Recordset_putref_Source(This,pvSource)	\
    ( (This)->lpVtbl -> putref_Source(This,pvSource) ) 

#define _Recordset_put_Source(This,pvSource)	\
    ( (This)->lpVtbl -> put_Source(This,pvSource) ) 

#define _Recordset_get_Source(This,pvSource)	\
    ( (This)->lpVtbl -> get_Source(This,pvSource) ) 

#define _Recordset_AddNew(This,FieldList,Values)	\
    ( (This)->lpVtbl -> AddNew(This,FieldList,Values) ) 

#define _Recordset_CancelUpdate(This)	\
    ( (This)->lpVtbl -> CancelUpdate(This) ) 

#define _Recordset_Close(This)	\
    ( (This)->lpVtbl -> Close(This) ) 

#define _Recordset_Delete(This,AffectRecords)	\
    ( (This)->lpVtbl -> Delete(This,AffectRecords) ) 

#define _Recordset_GetRows(This,Rows,Start,Fields,pvar)	\
    ( (This)->lpVtbl -> GetRows(This,Rows,Start,Fields,pvar) ) 

#define _Recordset_Move(This,NumRecords,Start)	\
    ( (This)->lpVtbl -> Move(This,NumRecords,Start) ) 

#define _Recordset_MoveNext(This)	\
    ( (This)->lpVtbl -> MoveNext(This) ) 

#define _Recordset_MovePrevious(This)	\
    ( (This)->lpVtbl -> MovePrevious(This) ) 

#define _Recordset_MoveFirst(This)	\
    ( (This)->lpVtbl -> MoveFirst(This) ) 

#define _Recordset_MoveLast(This)	\
    ( (This)->lpVtbl -> MoveLast(This) ) 

#define _Recordset_Open(This,Source,ActiveConnection,CursorType,LockType,Options)	\
    ( (This)->lpVtbl -> Open(This,Source,ActiveConnection,CursorType,LockType,Options) ) 

#define _Recordset_Requery(This,Options)	\
    ( (This)->lpVtbl -> Requery(This,Options) ) 

#define _Recordset__xResync(This,AffectRecords)	\
    ( (This)->lpVtbl -> _xResync(This,AffectRecords) ) 

#define _Recordset_Update(This,Fields,Values)	\
    ( (This)->lpVtbl -> Update(This,Fields,Values) ) 

#define _Recordset_get_AbsolutePage(This,pl)	\
    ( (This)->lpVtbl -> get_AbsolutePage(This,pl) ) 

#define _Recordset_put_AbsolutePage(This,pl)	\
    ( (This)->lpVtbl -> put_AbsolutePage(This,pl) ) 

#define _Recordset_get_EditMode(This,pl)	\
    ( (This)->lpVtbl -> get_EditMode(This,pl) ) 

#define _Recordset_get_Filter(This,Criteria)	\
    ( (This)->lpVtbl -> get_Filter(This,Criteria) ) 

#define _Recordset_put_Filter(This,Criteria)	\
    ( (This)->lpVtbl -> put_Filter(This,Criteria) ) 

#define _Recordset_get_PageCount(This,pl)	\
    ( (This)->lpVtbl -> get_PageCount(This,pl) ) 

#define _Recordset_get_PageSize(This,pl)	\
    ( (This)->lpVtbl -> get_PageSize(This,pl) ) 

#define _Recordset_put_PageSize(This,pl)	\
    ( (This)->lpVtbl -> put_PageSize(This,pl) ) 

#define _Recordset_get_Sort(This,Criteria)	\
    ( (This)->lpVtbl -> get_Sort(This,Criteria) ) 

#define _Recordset_put_Sort(This,Criteria)	\
    ( (This)->lpVtbl -> put_Sort(This,Criteria) ) 

#define _Recordset_get_Status(This,pl)	\
    ( (This)->lpVtbl -> get_Status(This,pl) ) 

#define _Recordset_get_State(This,plObjState)	\
    ( (This)->lpVtbl -> get_State(This,plObjState) ) 

#define _Recordset__xClone(This,ppvObject)	\
    ( (This)->lpVtbl -> _xClone(This,ppvObject) ) 

#define _Recordset_UpdateBatch(This,AffectRecords)	\
    ( (This)->lpVtbl -> UpdateBatch(This,AffectRecords) ) 

#define _Recordset_CancelBatch(This,AffectRecords)	\
    ( (This)->lpVtbl -> CancelBatch(This,AffectRecords) ) 

#define _Recordset_get_CursorLocation(This,plCursorLoc)	\
    ( (This)->lpVtbl -> get_CursorLocation(This,plCursorLoc) ) 

#define _Recordset_put_CursorLocation(This,plCursorLoc)	\
    ( (This)->lpVtbl -> put_CursorLocation(This,plCursorLoc) ) 

#define _Recordset_NextRecordset(This,RecordsAffected,ppiRs)	\
    ( (This)->lpVtbl -> NextRecordset(This,RecordsAffected,ppiRs) ) 

#define _Recordset_Supports(This,CursorOptions,pb)	\
    ( (This)->lpVtbl -> Supports(This,CursorOptions,pb) ) 

#define _Recordset_get_Collect(This,Index,pvar)	\
    ( (This)->lpVtbl -> get_Collect(This,Index,pvar) ) 

#define _Recordset_put_Collect(This,Index,pvar)	\
    ( (This)->lpVtbl -> put_Collect(This,Index,pvar) ) 

#define _Recordset_get_MarshalOptions(This,peMarshal)	\
    ( (This)->lpVtbl -> get_MarshalOptions(This,peMarshal) ) 

#define _Recordset_put_MarshalOptions(This,peMarshal)	\
    ( (This)->lpVtbl -> put_MarshalOptions(This,peMarshal) ) 

#define _Recordset_Find(This,Criteria,SkipRecords,SearchDirection,Start)	\
    ( (This)->lpVtbl -> Find(This,Criteria,SkipRecords,SearchDirection,Start) ) 


#define _Recordset_Cancel(This)	\
    ( (This)->lpVtbl -> Cancel(This) ) 

#define _Recordset_get_DataSource(This,ppunkDataSource)	\
    ( (This)->lpVtbl -> get_DataSource(This,ppunkDataSource) ) 

#define _Recordset_putref_DataSource(This,ppunkDataSource)	\
    ( (This)->lpVtbl -> putref_DataSource(This,ppunkDataSource) ) 

#define _Recordset__xSave(This,FileName,PersistFormat)	\
    ( (This)->lpVtbl -> _xSave(This,FileName,PersistFormat) ) 

#define _Recordset_get_ActiveCommand(This,ppCmd)	\
    ( (This)->lpVtbl -> get_ActiveCommand(This,ppCmd) ) 

#define _Recordset_put_StayInSync(This,pbStayInSync)	\
    ( (This)->lpVtbl -> put_StayInSync(This,pbStayInSync) ) 

#define _Recordset_get_StayInSync(This,pbStayInSync)	\
    ( (This)->lpVtbl -> get_StayInSync(This,pbStayInSync) ) 

#define _Recordset_GetString(This,StringFormat,NumRows,ColumnDelimeter,RowDelimeter,NullExpr,pRetString)	\
    ( (This)->lpVtbl -> GetString(This,StringFormat,NumRows,ColumnDelimeter,RowDelimeter,NullExpr,pRetString) ) 

#define _Recordset_get_DataMember(This,pbstrDataMember)	\
    ( (This)->lpVtbl -> get_DataMember(This,pbstrDataMember) ) 

#define _Recordset_put_DataMember(This,pbstrDataMember)	\
    ( (This)->lpVtbl -> put_DataMember(This,pbstrDataMember) ) 

#define _Recordset_CompareBookmarks(This,Bookmark1,Bookmark2,pCompare)	\
    ( (This)->lpVtbl -> CompareBookmarks(This,Bookmark1,Bookmark2,pCompare) ) 

#define _Recordset_Clone(This,LockType,ppvObject)	\
    ( (This)->lpVtbl -> Clone(This,LockType,ppvObject) ) 

#define _Recordset_Resync(This,AffectRecords,ResyncValues)	\
    ( (This)->lpVtbl -> Resync(This,AffectRecords,ResyncValues) ) 


#define _Recordset_Seek(This,KeyValues,SeekOption)	\
    ( (This)->lpVtbl -> Seek(This,KeyValues,SeekOption) ) 

#define _Recordset_put_Index(This,pbstrIndex)	\
    ( (This)->lpVtbl -> put_Index(This,pbstrIndex) ) 

#define _Recordset_get_Index(This,pbstrIndex)	\
    ( (This)->lpVtbl -> get_Index(This,pbstrIndex) ) 


#define _Recordset_Save(This,Destination,PersistFormat)	\
    ( (This)->lpVtbl -> Save(This,Destination,PersistFormat) ) 

#endif /* COBJMACROS */


#endif 	/* C style interface */




#endif 	/* ___Recordset_INTERFACE_DEFINED__ */


#ifndef __Fields15_INTERFACE_DEFINED__
#define __Fields15_INTERFACE_DEFINED__

/* interface Fields15 */
/* [object][oleautomation][nonextensible][dual][uuid] */ 


EXTERN_C const IID IID_Fields15;

#if defined(__cplusplus) && !defined(CINTERFACE)
    
    MIDL_INTERFACE("00000506-0000-0010-8000-00AA006D2EA4")
    Fields15 : public _Collection
    {
    public:
        virtual /* [propget][id] */ HRESULT __stdcall get_Item( 
            /* [in] */ VARIANT Index,
            /* [retval][out] */ Field **ppvObject) = 0;
        
    };
    
#else 	/* C style interface */

    typedef struct Fields15Vtbl
    {
        BEGIN_INTERFACE
        
        HRESULT ( STDMETHODCALLTYPE *QueryInterface )( 
            Fields15 * This,
            /* [in] */ REFIID riid,
            /* [annotation][iid_is][out] */ 
            __RPC__deref_out  void **ppvObject);
        
        ULONG ( STDMETHODCALLTYPE *AddRef )( 
            Fields15 * This);
        
        ULONG ( STDMETHODCALLTYPE *Release )( 
            Fields15 * This);
        
        HRESULT ( STDMETHODCALLTYPE *GetTypeInfoCount )( 
            Fields15 * This,
            /* [out] */ UINT *pctinfo);
        
        HRESULT ( STDMETHODCALLTYPE *GetTypeInfo )( 
            Fields15 * This,
            /* [in] */ UINT iTInfo,
            /* [in] */ LCID lcid,
            /* [out] */ ITypeInfo **ppTInfo);
        
        HRESULT ( STDMETHODCALLTYPE *GetIDsOfNames )( 
            Fields15 * This,
            /* [in] */ REFIID riid,
            /* [size_is][in] */ LPOLESTR *rgszNames,
            /* [range][in] */ UINT cNames,
            /* [in] */ LCID lcid,
            /* [size_is][out] */ DISPID *rgDispId);
        
        /* [local] */ HRESULT ( STDMETHODCALLTYPE *Invoke )( 
            Fields15 * This,
            /* [in] */ DISPID dispIdMember,
            /* [in] */ REFIID riid,
            /* [in] */ LCID lcid,
            /* [in] */ WORD wFlags,
            /* [out][in] */ DISPPARAMS *pDispParams,
            /* [out] */ VARIANT *pVarResult,
            /* [out] */ EXCEPINFO *pExcepInfo,
            /* [out] */ UINT *puArgErr);
        
        /* [propget][id] */ HRESULT ( __stdcall *get_Count )( 
            Fields15 * This,
            /* [retval][out] */ long *c);
        
        /* [restricted][id] */ HRESULT ( __stdcall *_NewEnum )( 
            Fields15 * This,
            /* [retval][out] */ IUnknown **ppvObject);
        
        /* [id] */ HRESULT ( __stdcall *Refresh )( 
            Fields15 * This);
        
        /* [propget][id] */ HRESULT ( __stdcall *get_Item )( 
            Fields15 * This,
            /* [in] */ VARIANT Index,
            /* [retval][out] */ Field **ppvObject);
        
        END_INTERFACE
    } Fields15Vtbl;

    interface Fields15
    {
        CONST_VTBL struct Fields15Vtbl *lpVtbl;
    };

    

#ifdef COBJMACROS


#define Fields15_QueryInterface(This,riid,ppvObject)	\
    ( (This)->lpVtbl -> QueryInterface(This,riid,ppvObject) ) 

#define Fields15_AddRef(This)	\
    ( (This)->lpVtbl -> AddRef(This) ) 

#define Fields15_Release(This)	\
    ( (This)->lpVtbl -> Release(This) ) 


#define Fields15_GetTypeInfoCount(This,pctinfo)	\
    ( (This)->lpVtbl -> GetTypeInfoCount(This,pctinfo) ) 

#define Fields15_GetTypeInfo(This,iTInfo,lcid,ppTInfo)	\
    ( (This)->lpVtbl -> GetTypeInfo(This,iTInfo,lcid,ppTInfo) ) 

#define Fields15_GetIDsOfNames(This,riid,rgszNames,cNames,lcid,rgDispId)	\
    ( (This)->lpVtbl -> GetIDsOfNames(This,riid,rgszNames,cNames,lcid,rgDispId) ) 

#define Fields15_Invoke(This,dispIdMember,riid,lcid,wFlags,pDispParams,pVarResult,pExcepInfo,puArgErr)	\
    ( (This)->lpVtbl -> Invoke(This,dispIdMember,riid,lcid,wFlags,pDispParams,pVarResult,pExcepInfo,puArgErr) ) 


#define Fields15_get_Count(This,c)	\
    ( (This)->lpVtbl -> get_Count(This,c) ) 

#define Fields15__NewEnum(This,ppvObject)	\
    ( (This)->lpVtbl -> _NewEnum(This,ppvObject) ) 

#define Fields15_Refresh(This)	\
    ( (This)->lpVtbl -> Refresh(This) ) 


#define Fields15_get_Item(This,Index,ppvObject)	\
    ( (This)->lpVtbl -> get_Item(This,Index,ppvObject) ) 

#endif /* COBJMACROS */


#endif 	/* C style interface */




#endif 	/* __Fields15_INTERFACE_DEFINED__ */


#ifndef __Fields20_INTERFACE_DEFINED__
#define __Fields20_INTERFACE_DEFINED__

/* interface Fields20 */
/* [object][oleautomation][nonextensible][dual][uuid] */ 


EXTERN_C const IID IID_Fields20;

#if defined(__cplusplus) && !defined(CINTERFACE)
    
    MIDL_INTERFACE("0000054D-0000-0010-8000-00AA006D2EA4")
    Fields20 : public Fields15
    {
    public:
        virtual /* [id] */ HRESULT __stdcall _Append( 
            /* [in] */ BSTR Name,
            /* [in] */ DataTypeEnum Type,
            /* [defaultvalue][in] */ ADO_LONGPTR DefinedSize = 0,
            /* [defaultvalue][in] */ FieldAttributeEnum Attrib = adFldUnspecified) = 0;
        
        virtual /* [id] */ HRESULT __stdcall Delete( 
            /* [in] */ VARIANT Index) = 0;
        
    };
    
#else 	/* C style interface */

    typedef struct Fields20Vtbl
    {
        BEGIN_INTERFACE
        
        HRESULT ( STDMETHODCALLTYPE *QueryInterface )( 
            Fields20 * This,
            /* [in] */ REFIID riid,
            /* [annotation][iid_is][out] */ 
            __RPC__deref_out  void **ppvObject);
        
        ULONG ( STDMETHODCALLTYPE *AddRef )( 
            Fields20 * This);
        
        ULONG ( STDMETHODCALLTYPE *Release )( 
            Fields20 * This);
        
        HRESULT ( STDMETHODCALLTYPE *GetTypeInfoCount )( 
            Fields20 * This,
            /* [out] */ UINT *pctinfo);
        
        HRESULT ( STDMETHODCALLTYPE *GetTypeInfo )( 
            Fields20 * This,
            /* [in] */ UINT iTInfo,
            /* [in] */ LCID lcid,
            /* [out] */ ITypeInfo **ppTInfo);
        
        HRESULT ( STDMETHODCALLTYPE *GetIDsOfNames )( 
            Fields20 * This,
            /* [in] */ REFIID riid,
            /* [size_is][in] */ LPOLESTR *rgszNames,
            /* [range][in] */ UINT cNames,
            /* [in] */ LCID lcid,
            /* [size_is][out] */ DISPID *rgDispId);
        
        /* [local] */ HRESULT ( STDMETHODCALLTYPE *Invoke )( 
            Fields20 * This,
            /* [in] */ DISPID dispIdMember,
            /* [in] */ REFIID riid,
            /* [in] */ LCID lcid,
            /* [in] */ WORD wFlags,
            /* [out][in] */ DISPPARAMS *pDispParams,
            /* [out] */ VARIANT *pVarResult,
            /* [out] */ EXCEPINFO *pExcepInfo,
            /* [out] */ UINT *puArgErr);
        
        /* [propget][id] */ HRESULT ( __stdcall *get_Count )( 
            Fields20 * This,
            /* [retval][out] */ long *c);
        
        /* [restricted][id] */ HRESULT ( __stdcall *_NewEnum )( 
            Fields20 * This,
            /* [retval][out] */ IUnknown **ppvObject);
        
        /* [id] */ HRESULT ( __stdcall *Refresh )( 
            Fields20 * This);
        
        /* [propget][id] */ HRESULT ( __stdcall *get_Item )( 
            Fields20 * This,
            /* [in] */ VARIANT Index,
            /* [retval][out] */ Field **ppvObject);
        
        /* [id] */ HRESULT ( __stdcall *_Append )( 
            Fields20 * This,
            /* [in] */ BSTR Name,
            /* [in] */ DataTypeEnum Type,
            /* [defaultvalue][in] */ ADO_LONGPTR DefinedSize,
            /* [defaultvalue][in] */ FieldAttributeEnum Attrib);
        
        /* [id] */ HRESULT ( __stdcall *Delete )( 
            Fields20 * This,
            /* [in] */ VARIANT Index);
        
        END_INTERFACE
    } Fields20Vtbl;

    interface Fields20
    {
        CONST_VTBL struct Fields20Vtbl *lpVtbl;
    };

    

#ifdef COBJMACROS


#define Fields20_QueryInterface(This,riid,ppvObject)	\
    ( (This)->lpVtbl -> QueryInterface(This,riid,ppvObject) ) 

#define Fields20_AddRef(This)	\
    ( (This)->lpVtbl -> AddRef(This) ) 

#define Fields20_Release(This)	\
    ( (This)->lpVtbl -> Release(This) ) 


#define Fields20_GetTypeInfoCount(This,pctinfo)	\
    ( (This)->lpVtbl -> GetTypeInfoCount(This,pctinfo) ) 

#define Fields20_GetTypeInfo(This,iTInfo,lcid,ppTInfo)	\
    ( (This)->lpVtbl -> GetTypeInfo(This,iTInfo,lcid,ppTInfo) ) 

#define Fields20_GetIDsOfNames(This,riid,rgszNames,cNames,lcid,rgDispId)	\
    ( (This)->lpVtbl -> GetIDsOfNames(This,riid,rgszNames,cNames,lcid,rgDispId) ) 

#define Fields20_Invoke(This,dispIdMember,riid,lcid,wFlags,pDispParams,pVarResult,pExcepInfo,puArgErr)	\
    ( (This)->lpVtbl -> Invoke(This,dispIdMember,riid,lcid,wFlags,pDispParams,pVarResult,pExcepInfo,puArgErr) ) 


#define Fields20_get_Count(This,c)	\
    ( (This)->lpVtbl -> get_Count(This,c) ) 

#define Fields20__NewEnum(This,ppvObject)	\
    ( (This)->lpVtbl -> _NewEnum(This,ppvObject) ) 

#define Fields20_Refresh(This)	\
    ( (This)->lpVtbl -> Refresh(This) ) 


#define Fields20_get_Item(This,Index,ppvObject)	\
    ( (This)->lpVtbl -> get_Item(This,Index,ppvObject) ) 


#define Fields20__Append(This,Name,Type,DefinedSize,Attrib)	\
    ( (This)->lpVtbl -> _Append(This,Name,Type,DefinedSize,Attrib) ) 

#define Fields20_Delete(This,Index)	\
    ( (This)->lpVtbl -> Delete(This,Index) ) 

#endif /* COBJMACROS */


#endif 	/* C style interface */




#endif 	/* __Fields20_INTERFACE_DEFINED__ */


#ifndef __Fields_INTERFACE_DEFINED__
#define __Fields_INTERFACE_DEFINED__

/* interface Fields */
/* [object][oleautomation][nonextensible][dual][uuid] */ 


EXTERN_C const IID IID_Fields;

#if defined(__cplusplus) && !defined(CINTERFACE)
    
    MIDL_INTERFACE("00000564-0000-0010-8000-00AA006D2EA4")
    Fields : public Fields20
    {
    public:
        virtual /* [id] */ HRESULT __stdcall Append( 
            /* [in] */ BSTR Name,
            /* [in] */ DataTypeEnum Type,
            /* [defaultvalue][in] */ ADO_LONGPTR DefinedSize,
            /* [defaultvalue][in] */ FieldAttributeEnum Attrib,
            /* [optional][in] */ VARIANT FieldValue) = 0;
        
        virtual /* [id] */ HRESULT __stdcall Update( void) = 0;
        
        virtual /* [id] */ HRESULT __stdcall Resync( 
            /* [defaultvalue][in] */ ResyncEnum ResyncValues = adResyncAllValues) = 0;
        
        virtual /* [id] */ HRESULT __stdcall CancelUpdate( void) = 0;
        
    };
    
#else 	/* C style interface */

    typedef struct FieldsVtbl
    {
        BEGIN_INTERFACE
        
        HRESULT ( STDMETHODCALLTYPE *QueryInterface )( 
            Fields * This,
            /* [in] */ REFIID riid,
            /* [annotation][iid_is][out] */ 
            __RPC__deref_out  void **ppvObject);
        
        ULONG ( STDMETHODCALLTYPE *AddRef )( 
            Fields * This);
        
        ULONG ( STDMETHODCALLTYPE *Release )( 
            Fields * This);
        
        HRESULT ( STDMETHODCALLTYPE *GetTypeInfoCount )( 
            Fields * This,
            /* [out] */ UINT *pctinfo);
        
        HRESULT ( STDMETHODCALLTYPE *GetTypeInfo )( 
            Fields * This,
            /* [in] */ UINT iTInfo,
            /* [in] */ LCID lcid,
            /* [out] */ ITypeInfo **ppTInfo);
        
        HRESULT ( STDMETHODCALLTYPE *GetIDsOfNames )( 
            Fields * This,
            /* [in] */ REFIID riid,
            /* [size_is][in] */ LPOLESTR *rgszNames,
            /* [range][in] */ UINT cNames,
            /* [in] */ LCID lcid,
            /* [size_is][out] */ DISPID *rgDispId);
        
        /* [local] */ HRESULT ( STDMETHODCALLTYPE *Invoke )( 
            Fields * This,
            /* [in] */ DISPID dispIdMember,
            /* [in] */ REFIID riid,
            /* [in] */ LCID lcid,
            /* [in] */ WORD wFlags,
            /* [out][in] */ DISPPARAMS *pDispParams,
            /* [out] */ VARIANT *pVarResult,
            /* [out] */ EXCEPINFO *pExcepInfo,
            /* [out] */ UINT *puArgErr);
        
        /* [propget][id] */ HRESULT ( __stdcall *get_Count )( 
            Fields * This,
            /* [retval][out] */ long *c);
        
        /* [restricted][id] */ HRESULT ( __stdcall *_NewEnum )( 
            Fields * This,
            /* [retval][out] */ IUnknown **ppvObject);
        
        /* [id] */ HRESULT ( __stdcall *Refresh )( 
            Fields * This);
        
        /* [propget][id] */ HRESULT ( __stdcall *get_Item )( 
            Fields * This,
            /* [in] */ VARIANT Index,
            /* [retval][out] */ Field **ppvObject);
        
        /* [id] */ HRESULT ( __stdcall *_Append )( 
            Fields * This,
            /* [in] */ BSTR Name,
            /* [in] */ DataTypeEnum Type,
            /* [defaultvalue][in] */ ADO_LONGPTR DefinedSize,
            /* [defaultvalue][in] */ FieldAttributeEnum Attrib);
        
        /* [id] */ HRESULT ( __stdcall *Delete )( 
            Fields * This,
            /* [in] */ VARIANT Index);
        
        /* [id] */ HRESULT ( __stdcall *Append )( 
            Fields * This,
            /* [in] */ BSTR Name,
            /* [in] */ DataTypeEnum Type,
            /* [defaultvalue][in] */ ADO_LONGPTR DefinedSize,
            /* [defaultvalue][in] */ FieldAttributeEnum Attrib,
            /* [optional][in] */ VARIANT FieldValue);
        
        /* [id] */ HRESULT ( __stdcall *Update )( 
            Fields * This);
        
        /* [id] */ HRESULT ( __stdcall *Resync )( 
            Fields * This,
            /* [defaultvalue][in] */ ResyncEnum ResyncValues);
        
        /* [id] */ HRESULT ( __stdcall *CancelUpdate )( 
            Fields * This);
        
        END_INTERFACE
    } FieldsVtbl;

    interface Fields
    {
        CONST_VTBL struct FieldsVtbl *lpVtbl;
    };

    

#ifdef COBJMACROS


#define Fields_QueryInterface(This,riid,ppvObject)	\
    ( (This)->lpVtbl -> QueryInterface(This,riid,ppvObject) ) 

#define Fields_AddRef(This)	\
    ( (This)->lpVtbl -> AddRef(This) ) 

#define Fields_Release(This)	\
    ( (This)->lpVtbl -> Release(This) ) 


#define Fields_GetTypeInfoCount(This,pctinfo)	\
    ( (This)->lpVtbl -> GetTypeInfoCount(This,pctinfo) ) 

#define Fields_GetTypeInfo(This,iTInfo,lcid,ppTInfo)	\
    ( (This)->lpVtbl -> GetTypeInfo(This,iTInfo,lcid,ppTInfo) ) 

#define Fields_GetIDsOfNames(This,riid,rgszNames,cNames,lcid,rgDispId)	\
    ( (This)->lpVtbl -> GetIDsOfNames(This,riid,rgszNames,cNames,lcid,rgDispId) ) 

#define Fields_Invoke(This,dispIdMember,riid,lcid,wFlags,pDispParams,pVarResult,pExcepInfo,puArgErr)	\
    ( (This)->lpVtbl -> Invoke(This,dispIdMember,riid,lcid,wFlags,pDispParams,pVarResult,pExcepInfo,puArgErr) ) 


#define Fields_get_Count(This,c)	\
    ( (This)->lpVtbl -> get_Count(This,c) ) 

#define Fields__NewEnum(This,ppvObject)	\
    ( (This)->lpVtbl -> _NewEnum(This,ppvObject) ) 

#define Fields_Refresh(This)	\
    ( (This)->lpVtbl -> Refresh(This) ) 


#define Fields_get_Item(This,Index,ppvObject)	\
    ( (This)->lpVtbl -> get_Item(This,Index,ppvObject) ) 


#define Fields__Append(This,Name,Type,DefinedSize,Attrib)	\
    ( (This)->lpVtbl -> _Append(This,Name,Type,DefinedSize,Attrib) ) 

#define Fields_Delete(This,Index)	\
    ( (This)->lpVtbl -> Delete(This,Index) ) 


#define Fields_Append(This,Name,Type,DefinedSize,Attrib,FieldValue)	\
    ( (This)->lpVtbl -> Append(This,Name,Type,DefinedSize,Attrib,FieldValue) ) 

#define Fields_Update(This)	\
    ( (This)->lpVtbl -> Update(This) ) 

#define Fields_Resync(This,ResyncValues)	\
    ( (This)->lpVtbl -> Resync(This,ResyncValues) ) 

#define Fields_CancelUpdate(This)	\
    ( (This)->lpVtbl -> CancelUpdate(This) ) 

#endif /* COBJMACROS */


#endif 	/* C style interface */




#endif 	/* __Fields_INTERFACE_DEFINED__ */


#ifndef __Field15_INTERFACE_DEFINED__
#define __Field15_INTERFACE_DEFINED__

/* interface Field15 */
/* [object][oleautomation][nonextensible][dual][uuid] */ 


EXTERN_C const IID IID_Field15;

#if defined(__cplusplus) && !defined(CINTERFACE)
    
    MIDL_INTERFACE("00000505-0000-0010-8000-00AA006D2EA4")
    Field15 : public _ADO
    {
    public:
        virtual /* [propget][id] */ HRESULT __stdcall get_ActualSize( 
            /* [retval][out] */ ADO_LONGPTR *pl) = 0;
        
        virtual /* [propget][id] */ HRESULT __stdcall get_Attributes( 
            /* [retval][out] */ long *pl) = 0;
        
        virtual /* [propget][id] */ HRESULT __stdcall get_DefinedSize( 
            /* [retval][out] */ ADO_LONGPTR *pl) = 0;
        
        virtual /* [propget][id] */ HRESULT __stdcall get_Name( 
            /* [retval][out] */ BSTR *pbstr) = 0;
        
        virtual /* [propget][id] */ HRESULT __stdcall get_Type( 
            /* [retval][out] */ DataTypeEnum *pDataType) = 0;
        
        virtual /* [propget][id] */ HRESULT __stdcall get_Value( 
            /* [retval][out] */ VARIANT *pvar) = 0;
        
        virtual /* [propput][id] */ HRESULT __stdcall put_Value( 
            /* [in] */ VARIANT pvar) = 0;
        
        virtual /* [propget][id] */ HRESULT __stdcall get_Precision( 
            /* [retval][out] */ unsigned char *pbPrecision) = 0;
        
        virtual /* [propget][id] */ HRESULT __stdcall get_NumericScale( 
            /* [retval][out] */ unsigned char *pbNumericScale) = 0;
        
        virtual /* [id] */ HRESULT __stdcall AppendChunk( 
            /* [in] */ VARIANT Data) = 0;
        
        virtual /* [id] */ HRESULT __stdcall GetChunk( 
            /* [in] */ long Length,
            /* [retval][out] */ VARIANT *pvar) = 0;
        
        virtual /* [propget][id] */ HRESULT __stdcall get_OriginalValue( 
            /* [retval][out] */ VARIANT *pvar) = 0;
        
        virtual /* [propget][id] */ HRESULT __stdcall get_UnderlyingValue( 
            /* [retval][out] */ VARIANT *pvar) = 0;
        
    };
    
#else 	/* C style interface */

    typedef struct Field15Vtbl
    {
        BEGIN_INTERFACE
        
        HRESULT ( STDMETHODCALLTYPE *QueryInterface )( 
            Field15 * This,
            /* [in] */ REFIID riid,
            /* [annotation][iid_is][out] */ 
            __RPC__deref_out  void **ppvObject);
        
        ULONG ( STDMETHODCALLTYPE *AddRef )( 
            Field15 * This);
        
        ULONG ( STDMETHODCALLTYPE *Release )( 
            Field15 * This);
        
        HRESULT ( STDMETHODCALLTYPE *GetTypeInfoCount )( 
            Field15 * This,
            /* [out] */ UINT *pctinfo);
        
        HRESULT ( STDMETHODCALLTYPE *GetTypeInfo )( 
            Field15 * This,
            /* [in] */ UINT iTInfo,
            /* [in] */ LCID lcid,
            /* [out] */ ITypeInfo **ppTInfo);
        
        HRESULT ( STDMETHODCALLTYPE *GetIDsOfNames )( 
            Field15 * This,
            /* [in] */ REFIID riid,
            /* [size_is][in] */ LPOLESTR *rgszNames,
            /* [range][in] */ UINT cNames,
            /* [in] */ LCID lcid,
            /* [size_is][out] */ DISPID *rgDispId);
        
        /* [local] */ HRESULT ( STDMETHODCALLTYPE *Invoke )( 
            Field15 * This,
            /* [in] */ DISPID dispIdMember,
            /* [in] */ REFIID riid,
            /* [in] */ LCID lcid,
            /* [in] */ WORD wFlags,
            /* [out][in] */ DISPPARAMS *pDispParams,
            /* [out] */ VARIANT *pVarResult,
            /* [out] */ EXCEPINFO *pExcepInfo,
            /* [out] */ UINT *puArgErr);
        
        /* [propget][id] */ HRESULT ( __stdcall *get_Properties )( 
            Field15 * This,
            /* [retval][out] */ Properties **ppvObject);
        
        /* [propget][id] */ HRESULT ( __stdcall *get_ActualSize )( 
            Field15 * This,
            /* [retval][out] */ ADO_LONGPTR *pl);
        
        /* [propget][id] */ HRESULT ( __stdcall *get_Attributes )( 
            Field15 * This,
            /* [retval][out] */ long *pl);
        
        /* [propget][id] */ HRESULT ( __stdcall *get_DefinedSize )( 
            Field15 * This,
            /* [retval][out] */ ADO_LONGPTR *pl);
        
        /* [propget][id] */ HRESULT ( __stdcall *get_Name )( 
            Field15 * This,
            /* [retval][out] */ BSTR *pbstr);
        
        /* [propget][id] */ HRESULT ( __stdcall *get_Type )( 
            Field15 * This,
            /* [retval][out] */ DataTypeEnum *pDataType);
        
        /* [propget][id] */ HRESULT ( __stdcall *get_Value )( 
            Field15 * This,
            /* [retval][out] */ VARIANT *pvar);
        
        /* [propput][id] */ HRESULT ( __stdcall *put_Value )( 
            Field15 * This,
            /* [in] */ VARIANT pvar);
        
        /* [propget][id] */ HRESULT ( __stdcall *get_Precision )( 
            Field15 * This,
            /* [retval][out] */ unsigned char *pbPrecision);
        
        /* [propget][id] */ HRESULT ( __stdcall *get_NumericScale )( 
            Field15 * This,
            /* [retval][out] */ unsigned char *pbNumericScale);
        
        /* [id] */ HRESULT ( __stdcall *AppendChunk )( 
            Field15 * This,
            /* [in] */ VARIANT Data);
        
        /* [id] */ HRESULT ( __stdcall *GetChunk )( 
            Field15 * This,
            /* [in] */ long Length,
            /* [retval][out] */ VARIANT *pvar);
        
        /* [propget][id] */ HRESULT ( __stdcall *get_OriginalValue )( 
            Field15 * This,
            /* [retval][out] */ VARIANT *pvar);
        
        /* [propget][id] */ HRESULT ( __stdcall *get_UnderlyingValue )( 
            Field15 * This,
            /* [retval][out] */ VARIANT *pvar);
        
        END_INTERFACE
    } Field15Vtbl;

    interface Field15
    {
        CONST_VTBL struct Field15Vtbl *lpVtbl;
    };

    

#ifdef COBJMACROS


#define Field15_QueryInterface(This,riid,ppvObject)	\
    ( (This)->lpVtbl -> QueryInterface(This,riid,ppvObject) ) 

#define Field15_AddRef(This)	\
    ( (This)->lpVtbl -> AddRef(This) ) 

#define Field15_Release(This)	\
    ( (This)->lpVtbl -> Release(This) ) 


#define Field15_GetTypeInfoCount(This,pctinfo)	\
    ( (This)->lpVtbl -> GetTypeInfoCount(This,pctinfo) ) 

#define Field15_GetTypeInfo(This,iTInfo,lcid,ppTInfo)	\
    ( (This)->lpVtbl -> GetTypeInfo(This,iTInfo,lcid,ppTInfo) ) 

#define Field15_GetIDsOfNames(This,riid,rgszNames,cNames,lcid,rgDispId)	\
    ( (This)->lpVtbl -> GetIDsOfNames(This,riid,rgszNames,cNames,lcid,rgDispId) ) 

#define Field15_Invoke(This,dispIdMember,riid,lcid,wFlags,pDispParams,pVarResult,pExcepInfo,puArgErr)	\
    ( (This)->lpVtbl -> Invoke(This,dispIdMember,riid,lcid,wFlags,pDispParams,pVarResult,pExcepInfo,puArgErr) ) 


#define Field15_get_Properties(This,ppvObject)	\
    ( (This)->lpVtbl -> get_Properties(This,ppvObject) ) 


#define Field15_get_ActualSize(This,pl)	\
    ( (This)->lpVtbl -> get_ActualSize(This,pl) ) 

#define Field15_get_Attributes(This,pl)	\
    ( (This)->lpVtbl -> get_Attributes(This,pl) ) 

#define Field15_get_DefinedSize(This,pl)	\
    ( (This)->lpVtbl -> get_DefinedSize(This,pl) ) 

#define Field15_get_Name(This,pbstr)	\
    ( (This)->lpVtbl -> get_Name(This,pbstr) ) 

#define Field15_get_Type(This,pDataType)	\
    ( (This)->lpVtbl -> get_Type(This,pDataType) ) 

#define Field15_get_Value(This,pvar)	\
    ( (This)->lpVtbl -> get_Value(This,pvar) ) 

#define Field15_put_Value(This,pvar)	\
    ( (This)->lpVtbl -> put_Value(This,pvar) ) 

#define Field15_get_Precision(This,pbPrecision)	\
    ( (This)->lpVtbl -> get_Precision(This,pbPrecision) ) 

#define Field15_get_NumericScale(This,pbNumericScale)	\
    ( (This)->lpVtbl -> get_NumericScale(This,pbNumericScale) ) 

#define Field15_AppendChunk(This,Data)	\
    ( (This)->lpVtbl -> AppendChunk(This,Data) ) 

#define Field15_GetChunk(This,Length,pvar)	\
    ( (This)->lpVtbl -> GetChunk(This,Length,pvar) ) 

#define Field15_get_OriginalValue(This,pvar)	\
    ( (This)->lpVtbl -> get_OriginalValue(This,pvar) ) 

#define Field15_get_UnderlyingValue(This,pvar)	\
    ( (This)->lpVtbl -> get_UnderlyingValue(This,pvar) ) 

#endif /* COBJMACROS */


#endif 	/* C style interface */




#endif 	/* __Field15_INTERFACE_DEFINED__ */


#ifndef __Field20_INTERFACE_DEFINED__
#define __Field20_INTERFACE_DEFINED__

/* interface Field20 */
/* [object][hidden][oleautomation][nonextensible][dual][uuid] */ 


EXTERN_C const IID IID_Field20;

#if defined(__cplusplus) && !defined(CINTERFACE)
    
    MIDL_INTERFACE("0000054C-0000-0010-8000-00AA006D2EA4")
    Field20 : public _ADO
    {
    public:
        virtual /* [propget][id] */ HRESULT __stdcall get_ActualSize( 
            /* [retval][out] */ ADO_LONGPTR *pl) = 0;
        
        virtual /* [propget][id] */ HRESULT __stdcall get_Attributes( 
            /* [retval][out] */ long *pl) = 0;
        
        virtual /* [propget][id] */ HRESULT __stdcall get_DefinedSize( 
            /* [retval][out] */ ADO_LONGPTR *pl) = 0;
        
        virtual /* [propget][id] */ HRESULT __stdcall get_Name( 
            /* [retval][out] */ BSTR *pbstr) = 0;
        
        virtual /* [propget][id] */ HRESULT __stdcall get_Type( 
            /* [retval][out] */ DataTypeEnum *pDataType) = 0;
        
        virtual /* [propget][id] */ HRESULT __stdcall get_Value( 
            /* [retval][out] */ VARIANT *pvar) = 0;
        
        virtual /* [propput][id] */ HRESULT __stdcall put_Value( 
            /* [in] */ VARIANT pvar) = 0;
        
        virtual /* [propget][id] */ HRESULT __stdcall get_Precision( 
            /* [retval][out] */ unsigned char *pbPrecision) = 0;
        
        virtual /* [propget][id] */ HRESULT __stdcall get_NumericScale( 
            /* [retval][out] */ unsigned char *pbNumericScale) = 0;
        
        virtual /* [id] */ HRESULT __stdcall AppendChunk( 
            /* [in] */ VARIANT Data) = 0;
        
        virtual /* [id] */ HRESULT __stdcall GetChunk( 
            /* [in] */ long Length,
            /* [retval][out] */ VARIANT *pvar) = 0;
        
        virtual /* [propget][id] */ HRESULT __stdcall get_OriginalValue( 
            /* [retval][out] */ VARIANT *pvar) = 0;
        
        virtual /* [propget][id] */ HRESULT __stdcall get_UnderlyingValue( 
            /* [retval][out] */ VARIANT *pvar) = 0;
        
        virtual /* [propget][id] */ HRESULT __stdcall get_DataFormat( 
            /* [retval][out] */ IUnknown **ppiDF) = 0;
        
        virtual /* [propputref][id] */ HRESULT __stdcall putref_DataFormat( 
            /* [in] */ IUnknown *ppiDF) = 0;
        
        virtual /* [propput][id] */ HRESULT __stdcall put_Precision( 
            /* [in] */ unsigned char pbPrecision) = 0;
        
        virtual /* [propput][id] */ HRESULT __stdcall put_NumericScale( 
            /* [in] */ unsigned char pbNumericScale) = 0;
        
        virtual /* [propput][id] */ HRESULT __stdcall put_Type( 
            /* [in] */ DataTypeEnum pDataType) = 0;
        
        virtual /* [propput][id] */ HRESULT __stdcall put_DefinedSize( 
            /* [in] */ ADO_LONGPTR pl) = 0;
        
        virtual /* [propput][id] */ HRESULT __stdcall put_Attributes( 
            /* [in] */ long pl) = 0;
        
    };
    
#else 	/* C style interface */

    typedef struct Field20Vtbl
    {
        BEGIN_INTERFACE
        
        HRESULT ( STDMETHODCALLTYPE *QueryInterface )( 
            Field20 * This,
            /* [in] */ REFIID riid,
            /* [annotation][iid_is][out] */ 
            __RPC__deref_out  void **ppvObject);
        
        ULONG ( STDMETHODCALLTYPE *AddRef )( 
            Field20 * This);
        
        ULONG ( STDMETHODCALLTYPE *Release )( 
            Field20 * This);
        
        HRESULT ( STDMETHODCALLTYPE *GetTypeInfoCount )( 
            Field20 * This,
            /* [out] */ UINT *pctinfo);
        
        HRESULT ( STDMETHODCALLTYPE *GetTypeInfo )( 
            Field20 * This,
            /* [in] */ UINT iTInfo,
            /* [in] */ LCID lcid,
            /* [out] */ ITypeInfo **ppTInfo);
        
        HRESULT ( STDMETHODCALLTYPE *GetIDsOfNames )( 
            Field20 * This,
            /* [in] */ REFIID riid,
            /* [size_is][in] */ LPOLESTR *rgszNames,
            /* [range][in] */ UINT cNames,
            /* [in] */ LCID lcid,
            /* [size_is][out] */ DISPID *rgDispId);
        
        /* [local] */ HRESULT ( STDMETHODCALLTYPE *Invoke )( 
            Field20 * This,
            /* [in] */ DISPID dispIdMember,
            /* [in] */ REFIID riid,
            /* [in] */ LCID lcid,
            /* [in] */ WORD wFlags,
            /* [out][in] */ DISPPARAMS *pDispParams,
            /* [out] */ VARIANT *pVarResult,
            /* [out] */ EXCEPINFO *pExcepInfo,
            /* [out] */ UINT *puArgErr);
        
        /* [propget][id] */ HRESULT ( __stdcall *get_Properties )( 
            Field20 * This,
            /* [retval][out] */ Properties **ppvObject);
        
        /* [propget][id] */ HRESULT ( __stdcall *get_ActualSize )( 
            Field20 * This,
            /* [retval][out] */ ADO_LONGPTR *pl);
        
        /* [propget][id] */ HRESULT ( __stdcall *get_Attributes )( 
            Field20 * This,
            /* [retval][out] */ long *pl);
        
        /* [propget][id] */ HRESULT ( __stdcall *get_DefinedSize )( 
            Field20 * This,
            /* [retval][out] */ ADO_LONGPTR *pl);
        
        /* [propget][id] */ HRESULT ( __stdcall *get_Name )( 
            Field20 * This,
            /* [retval][out] */ BSTR *pbstr);
        
        /* [propget][id] */ HRESULT ( __stdcall *get_Type )( 
            Field20 * This,
            /* [retval][out] */ DataTypeEnum *pDataType);
        
        /* [propget][id] */ HRESULT ( __stdcall *get_Value )( 
            Field20 * This,
            /* [retval][out] */ VARIANT *pvar);
        
        /* [propput][id] */ HRESULT ( __stdcall *put_Value )( 
            Field20 * This,
            /* [in] */ VARIANT pvar);
        
        /* [propget][id] */ HRESULT ( __stdcall *get_Precision )( 
            Field20 * This,
            /* [retval][out] */ unsigned char *pbPrecision);
        
        /* [propget][id] */ HRESULT ( __stdcall *get_NumericScale )( 
            Field20 * This,
            /* [retval][out] */ unsigned char *pbNumericScale);
        
        /* [id] */ HRESULT ( __stdcall *AppendChunk )( 
            Field20 * This,
            /* [in] */ VARIANT Data);
        
        /* [id] */ HRESULT ( __stdcall *GetChunk )( 
            Field20 * This,
            /* [in] */ long Length,
            /* [retval][out] */ VARIANT *pvar);
        
        /* [propget][id] */ HRESULT ( __stdcall *get_OriginalValue )( 
            Field20 * This,
            /* [retval][out] */ VARIANT *pvar);
        
        /* [propget][id] */ HRESULT ( __stdcall *get_UnderlyingValue )( 
            Field20 * This,
            /* [retval][out] */ VARIANT *pvar);
        
        /* [propget][id] */ HRESULT ( __stdcall *get_DataFormat )( 
            Field20 * This,
            /* [retval][out] */ IUnknown **ppiDF);
        
        /* [propputref][id] */ HRESULT ( __stdcall *putref_DataFormat )( 
            Field20 * This,
            /* [in] */ IUnknown *ppiDF);
        
        /* [propput][id] */ HRESULT ( __stdcall *put_Precision )( 
            Field20 * This,
            /* [in] */ unsigned char pbPrecision);
        
        /* [propput][id] */ HRESULT ( __stdcall *put_NumericScale )( 
            Field20 * This,
            /* [in] */ unsigned char pbNumericScale);
        
        /* [propput][id] */ HRESULT ( __stdcall *put_Type )( 
            Field20 * This,
            /* [in] */ DataTypeEnum pDataType);
        
        /* [propput][id] */ HRESULT ( __stdcall *put_DefinedSize )( 
            Field20 * This,
            /* [in] */ ADO_LONGPTR pl);
        
        /* [propput][id] */ HRESULT ( __stdcall *put_Attributes )( 
            Field20 * This,
            /* [in] */ long pl);
        
        END_INTERFACE
    } Field20Vtbl;

    interface Field20
    {
        CONST_VTBL struct Field20Vtbl *lpVtbl;
    };

    

#ifdef COBJMACROS


#define Field20_QueryInterface(This,riid,ppvObject)	\
    ( (This)->lpVtbl -> QueryInterface(This,riid,ppvObject) ) 

#define Field20_AddRef(This)	\
    ( (This)->lpVtbl -> AddRef(This) ) 

#define Field20_Release(This)	\
    ( (This)->lpVtbl -> Release(This) ) 


#define Field20_GetTypeInfoCount(This,pctinfo)	\
    ( (This)->lpVtbl -> GetTypeInfoCount(This,pctinfo) ) 

#define Field20_GetTypeInfo(This,iTInfo,lcid,ppTInfo)	\
    ( (This)->lpVtbl -> GetTypeInfo(This,iTInfo,lcid,ppTInfo) ) 

#define Field20_GetIDsOfNames(This,riid,rgszNames,cNames,lcid,rgDispId)	\
    ( (This)->lpVtbl -> GetIDsOfNames(This,riid,rgszNames,cNames,lcid,rgDispId) ) 

#define Field20_Invoke(This,dispIdMember,riid,lcid,wFlags,pDispParams,pVarResult,pExcepInfo,puArgErr)	\
    ( (This)->lpVtbl -> Invoke(This,dispIdMember,riid,lcid,wFlags,pDispParams,pVarResult,pExcepInfo,puArgErr) ) 


#define Field20_get_Properties(This,ppvObject)	\
    ( (This)->lpVtbl -> get_Properties(This,ppvObject) ) 


#define Field20_get_ActualSize(This,pl)	\
    ( (This)->lpVtbl -> get_ActualSize(This,pl) ) 

#define Field20_get_Attributes(This,pl)	\
    ( (This)->lpVtbl -> get_Attributes(This,pl) ) 

#define Field20_get_DefinedSize(This,pl)	\
    ( (This)->lpVtbl -> get_DefinedSize(This,pl) ) 

#define Field20_get_Name(This,pbstr)	\
    ( (This)->lpVtbl -> get_Name(This,pbstr) ) 

#define Field20_get_Type(This,pDataType)	\
    ( (This)->lpVtbl -> get_Type(This,pDataType) ) 

#define Field20_get_Value(This,pvar)	\
    ( (This)->lpVtbl -> get_Value(This,pvar) ) 

#define Field20_put_Value(This,pvar)	\
    ( (This)->lpVtbl -> put_Value(This,pvar) ) 

#define Field20_get_Precision(This,pbPrecision)	\
    ( (This)->lpVtbl -> get_Precision(This,pbPrecision) ) 

#define Field20_get_NumericScale(This,pbNumericScale)	\
    ( (This)->lpVtbl -> get_NumericScale(This,pbNumericScale) ) 

#define Field20_AppendChunk(This,Data)	\
    ( (This)->lpVtbl -> AppendChunk(This,Data) ) 

#define Field20_GetChunk(This,Length,pvar)	\
    ( (This)->lpVtbl -> GetChunk(This,Length,pvar) ) 

#define Field20_get_OriginalValue(This,pvar)	\
    ( (This)->lpVtbl -> get_OriginalValue(This,pvar) ) 

#define Field20_get_UnderlyingValue(This,pvar)	\
    ( (This)->lpVtbl -> get_UnderlyingValue(This,pvar) ) 

#define Field20_get_DataFormat(This,ppiDF)	\
    ( (This)->lpVtbl -> get_DataFormat(This,ppiDF) ) 

#define Field20_putref_DataFormat(This,ppiDF)	\
    ( (This)->lpVtbl -> putref_DataFormat(This,ppiDF) ) 

#define Field20_put_Precision(This,pbPrecision)	\
    ( (This)->lpVtbl -> put_Precision(This,pbPrecision) ) 

#define Field20_put_NumericScale(This,pbNumericScale)	\
    ( (This)->lpVtbl -> put_NumericScale(This,pbNumericScale) ) 

#define Field20_put_Type(This,pDataType)	\
    ( (This)->lpVtbl -> put_Type(This,pDataType) ) 

#define Field20_put_DefinedSize(This,pl)	\
    ( (This)->lpVtbl -> put_DefinedSize(This,pl) ) 

#define Field20_put_Attributes(This,pl)	\
    ( (This)->lpVtbl -> put_Attributes(This,pl) ) 

#endif /* COBJMACROS */


#endif 	/* C style interface */




#endif 	/* __Field20_INTERFACE_DEFINED__ */


#ifndef __Field_INTERFACE_DEFINED__
#define __Field_INTERFACE_DEFINED__

/* interface Field */
/* [object][oleautomation][nonextensible][dual][uuid] */ 


EXTERN_C const IID IID_Field;

#if defined(__cplusplus) && !defined(CINTERFACE)
    
    MIDL_INTERFACE("00000569-0000-0010-8000-00AA006D2EA4")
    Field : public Field20
    {
    public:
        virtual /* [propget][id] */ HRESULT __stdcall get_Status( 
            /* [retval][out] */ long *pFStatus) = 0;
        
    };
    
#else 	/* C style interface */

    typedef struct FieldVtbl
    {
        BEGIN_INTERFACE
        
        HRESULT ( STDMETHODCALLTYPE *QueryInterface )( 
            Field * This,
            /* [in] */ REFIID riid,
            /* [annotation][iid_is][out] */ 
            __RPC__deref_out  void **ppvObject);
        
        ULONG ( STDMETHODCALLTYPE *AddRef )( 
            Field * This);
        
        ULONG ( STDMETHODCALLTYPE *Release )( 
            Field * This);
        
        HRESULT ( STDMETHODCALLTYPE *GetTypeInfoCount )( 
            Field * This,
            /* [out] */ UINT *pctinfo);
        
        HRESULT ( STDMETHODCALLTYPE *GetTypeInfo )( 
            Field * This,
            /* [in] */ UINT iTInfo,
            /* [in] */ LCID lcid,
            /* [out] */ ITypeInfo **ppTInfo);
        
        HRESULT ( STDMETHODCALLTYPE *GetIDsOfNames )( 
            Field * This,
            /* [in] */ REFIID riid,
            /* [size_is][in] */ LPOLESTR *rgszNames,
            /* [range][in] */ UINT cNames,
            /* [in] */ LCID lcid,
            /* [size_is][out] */ DISPID *rgDispId);
        
        /* [local] */ HRESULT ( STDMETHODCALLTYPE *Invoke )( 
            Field * This,
            /* [in] */ DISPID dispIdMember,
            /* [in] */ REFIID riid,
            /* [in] */ LCID lcid,
            /* [in] */ WORD wFlags,
            /* [out][in] */ DISPPARAMS *pDispParams,
            /* [out] */ VARIANT *pVarResult,
            /* [out] */ EXCEPINFO *pExcepInfo,
            /* [out] */ UINT *puArgErr);
        
        /* [propget][id] */ HRESULT ( __stdcall *get_Properties )( 
            Field * This,
            /* [retval][out] */ Properties **ppvObject);
        
        /* [propget][id] */ HRESULT ( __stdcall *get_ActualSize )( 
            Field * This,
            /* [retval][out] */ ADO_LONGPTR *pl);
        
        /* [propget][id] */ HRESULT ( __stdcall *get_Attributes )( 
            Field * This,
            /* [retval][out] */ long *pl);
        
        /* [propget][id] */ HRESULT ( __stdcall *get_DefinedSize )( 
            Field * This,
            /* [retval][out] */ ADO_LONGPTR *pl);
        
        /* [propget][id] */ HRESULT ( __stdcall *get_Name )( 
            Field * This,
            /* [retval][out] */ BSTR *pbstr);
        
        /* [propget][id] */ HRESULT ( __stdcall *get_Type )( 
            Field * This,
            /* [retval][out] */ DataTypeEnum *pDataType);
        
        /* [propget][id] */ HRESULT ( __stdcall *get_Value )( 
            Field * This,
            /* [retval][out] */ VARIANT *pvar);
        
        /* [propput][id] */ HRESULT ( __stdcall *put_Value )( 
            Field * This,
            /* [in] */ VARIANT pvar);
        
        /* [propget][id] */ HRESULT ( __stdcall *get_Precision )( 
            Field * This,
            /* [retval][out] */ unsigned char *pbPrecision);
        
        /* [propget][id] */ HRESULT ( __stdcall *get_NumericScale )( 
            Field * This,
            /* [retval][out] */ unsigned char *pbNumericScale);
        
        /* [id] */ HRESULT ( __stdcall *AppendChunk )( 
            Field * This,
            /* [in] */ VARIANT Data);
        
        /* [id] */ HRESULT ( __stdcall *GetChunk )( 
            Field * This,
            /* [in] */ long Length,
            /* [retval][out] */ VARIANT *pvar);
        
        /* [propget][id] */ HRESULT ( __stdcall *get_OriginalValue )( 
            Field * This,
            /* [retval][out] */ VARIANT *pvar);
        
        /* [propget][id] */ HRESULT ( __stdcall *get_UnderlyingValue )( 
            Field * This,
            /* [retval][out] */ VARIANT *pvar);
        
        /* [propget][id] */ HRESULT ( __stdcall *get_DataFormat )( 
            Field * This,
            /* [retval][out] */ IUnknown **ppiDF);
        
        /* [propputref][id] */ HRESULT ( __stdcall *putref_DataFormat )( 
            Field * This,
            /* [in] */ IUnknown *ppiDF);
        
        /* [propput][id] */ HRESULT ( __stdcall *put_Precision )( 
            Field * This,
            /* [in] */ unsigned char pbPrecision);
        
        /* [propput][id] */ HRESULT ( __stdcall *put_NumericScale )( 
            Field * This,
            /* [in] */ unsigned char pbNumericScale);
        
        /* [propput][id] */ HRESULT ( __stdcall *put_Type )( 
            Field * This,
            /* [in] */ DataTypeEnum pDataType);
        
        /* [propput][id] */ HRESULT ( __stdcall *put_DefinedSize )( 
            Field * This,
            /* [in] */ ADO_LONGPTR pl);
        
        /* [propput][id] */ HRESULT ( __stdcall *put_Attributes )( 
            Field * This,
            /* [in] */ long pl);
        
        /* [propget][id] */ HRESULT ( __stdcall *get_Status )( 
            Field * This,
            /* [retval][out] */ long *pFStatus);
        
        END_INTERFACE
    } FieldVtbl;

    interface Field
    {
        CONST_VTBL struct FieldVtbl *lpVtbl;
    };

    

#ifdef COBJMACROS


#define Field_QueryInterface(This,riid,ppvObject)	\
    ( (This)->lpVtbl -> QueryInterface(This,riid,ppvObject) ) 

#define Field_AddRef(This)	\
    ( (This)->lpVtbl -> AddRef(This) ) 

#define Field_Release(This)	\
    ( (This)->lpVtbl -> Release(This) ) 


#define Field_GetTypeInfoCount(This,pctinfo)	\
    ( (This)->lpVtbl -> GetTypeInfoCount(This,pctinfo) ) 

#define Field_GetTypeInfo(This,iTInfo,lcid,ppTInfo)	\
    ( (This)->lpVtbl -> GetTypeInfo(This,iTInfo,lcid,ppTInfo) ) 

#define Field_GetIDsOfNames(This,riid,rgszNames,cNames,lcid,rgDispId)	\
    ( (This)->lpVtbl -> GetIDsOfNames(This,riid,rgszNames,cNames,lcid,rgDispId) ) 

#define Field_Invoke(This,dispIdMember,riid,lcid,wFlags,pDispParams,pVarResult,pExcepInfo,puArgErr)	\
    ( (This)->lpVtbl -> Invoke(This,dispIdMember,riid,lcid,wFlags,pDispParams,pVarResult,pExcepInfo,puArgErr) ) 


#define Field_get_Properties(This,ppvObject)	\
    ( (This)->lpVtbl -> get_Properties(This,ppvObject) ) 


#define Field_get_ActualSize(This,pl)	\
    ( (This)->lpVtbl -> get_ActualSize(This,pl) ) 

#define Field_get_Attributes(This,pl)	\
    ( (This)->lpVtbl -> get_Attributes(This,pl) ) 

#define Field_get_DefinedSize(This,pl)	\
    ( (This)->lpVtbl -> get_DefinedSize(This,pl) ) 

#define Field_get_Name(This,pbstr)	\
    ( (This)->lpVtbl -> get_Name(This,pbstr) ) 

#define Field_get_Type(This,pDataType)	\
    ( (This)->lpVtbl -> get_Type(This,pDataType) ) 

#define Field_get_Value(This,pvar)	\
    ( (This)->lpVtbl -> get_Value(This,pvar) ) 

#define Field_put_Value(This,pvar)	\
    ( (This)->lpVtbl -> put_Value(This,pvar) ) 

#define Field_get_Precision(This,pbPrecision)	\
    ( (This)->lpVtbl -> get_Precision(This,pbPrecision) ) 

#define Field_get_NumericScale(This,pbNumericScale)	\
    ( (This)->lpVtbl -> get_NumericScale(This,pbNumericScale) ) 

#define Field_AppendChunk(This,Data)	\
    ( (This)->lpVtbl -> AppendChunk(This,Data) ) 

#define Field_GetChunk(This,Length,pvar)	\
    ( (This)->lpVtbl -> GetChunk(This,Length,pvar) ) 

#define Field_get_OriginalValue(This,pvar)	\
    ( (This)->lpVtbl -> get_OriginalValue(This,pvar) ) 

#define Field_get_UnderlyingValue(This,pvar)	\
    ( (This)->lpVtbl -> get_UnderlyingValue(This,pvar) ) 

#define Field_get_DataFormat(This,ppiDF)	\
    ( (This)->lpVtbl -> get_DataFormat(This,ppiDF) ) 

#define Field_putref_DataFormat(This,ppiDF)	\
    ( (This)->lpVtbl -> putref_DataFormat(This,ppiDF) ) 

#define Field_put_Precision(This,pbPrecision)	\
    ( (This)->lpVtbl -> put_Precision(This,pbPrecision) ) 

#define Field_put_NumericScale(This,pbNumericScale)	\
    ( (This)->lpVtbl -> put_NumericScale(This,pbNumericScale) ) 

#define Field_put_Type(This,pDataType)	\
    ( (This)->lpVtbl -> put_Type(This,pDataType) ) 

#define Field_put_DefinedSize(This,pl)	\
    ( (This)->lpVtbl -> put_DefinedSize(This,pl) ) 

#define Field_put_Attributes(This,pl)	\
    ( (This)->lpVtbl -> put_Attributes(This,pl) ) 


#define Field_get_Status(This,pFStatus)	\
    ( (This)->lpVtbl -> get_Status(This,pFStatus) ) 

#endif /* COBJMACROS */


#endif 	/* C style interface */




#endif 	/* __Field_INTERFACE_DEFINED__ */


#ifndef ___Parameter_INTERFACE_DEFINED__
#define ___Parameter_INTERFACE_DEFINED__

/* interface _Parameter */
/* [object][oleautomation][nonextensible][dual][uuid] */ 


EXTERN_C const IID IID__Parameter;

#if defined(__cplusplus) && !defined(CINTERFACE)
    
    MIDL_INTERFACE("0000050C-0000-0010-8000-00AA006D2EA4")
    _Parameter : public _ADO
    {
    public:
        virtual /* [propget][id] */ HRESULT __stdcall get_Name( 
            /* [retval][out] */ BSTR *pbstr) = 0;
        
        virtual /* [propput][id] */ HRESULT __stdcall put_Name( 
            /* [in] */ BSTR pbstr) = 0;
        
        virtual /* [propget][id] */ HRESULT __stdcall get_Value( 
            /* [retval][out] */ VARIANT *pvar) = 0;
        
        virtual /* [propput][id] */ HRESULT __stdcall put_Value( 
            /* [in] */ VARIANT pvar) = 0;
        
        virtual /* [propget][id] */ HRESULT __stdcall get_Type( 
            /* [retval][out] */ DataTypeEnum *psDataType) = 0;
        
        virtual /* [propput][id] */ HRESULT __stdcall put_Type( 
            /* [in] */ DataTypeEnum psDataType) = 0;
        
        virtual /* [propput][id] */ HRESULT __stdcall put_Direction( 
            /* [in] */ ParameterDirectionEnum plParmDirection) = 0;
        
        virtual /* [propget][id] */ HRESULT __stdcall get_Direction( 
            /* [retval][out] */ ParameterDirectionEnum *plParmDirection) = 0;
        
        virtual /* [propput][id] */ HRESULT __stdcall put_Precision( 
            /* [in] */ unsigned char pbPrecision) = 0;
        
        virtual /* [propget][id] */ HRESULT __stdcall get_Precision( 
            /* [retval][out] */ unsigned char *pbPrecision) = 0;
        
        virtual /* [propput][id] */ HRESULT __stdcall put_NumericScale( 
            /* [in] */ unsigned char pbScale) = 0;
        
        virtual /* [propget][id] */ HRESULT __stdcall get_NumericScale( 
            /* [retval][out] */ unsigned char *pbScale) = 0;
        
        virtual /* [propput][id] */ HRESULT __stdcall put_Size( 
            /* [in] */ ADO_LONGPTR pl) = 0;
        
        virtual /* [propget][id] */ HRESULT __stdcall get_Size( 
            /* [retval][out] */ ADO_LONGPTR *pl) = 0;
        
        virtual /* [id] */ HRESULT __stdcall AppendChunk( 
            /* [in] */ VARIANT Val) = 0;
        
        virtual /* [propget][id] */ HRESULT __stdcall get_Attributes( 
            /* [retval][out] */ long *plParmAttribs) = 0;
        
        virtual /* [propput][id] */ HRESULT __stdcall put_Attributes( 
            /* [in] */ long plParmAttribs) = 0;
        
    };
    
#else 	/* C style interface */

    typedef struct _ParameterVtbl
    {
        BEGIN_INTERFACE
        
        HRESULT ( STDMETHODCALLTYPE *QueryInterface )( 
            _Parameter * This,
            /* [in] */ REFIID riid,
            /* [annotation][iid_is][out] */ 
            __RPC__deref_out  void **ppvObject);
        
        ULONG ( STDMETHODCALLTYPE *AddRef )( 
            _Parameter * This);
        
        ULONG ( STDMETHODCALLTYPE *Release )( 
            _Parameter * This);
        
        HRESULT ( STDMETHODCALLTYPE *GetTypeInfoCount )( 
            _Parameter * This,
            /* [out] */ UINT *pctinfo);
        
        HRESULT ( STDMETHODCALLTYPE *GetTypeInfo )( 
            _Parameter * This,
            /* [in] */ UINT iTInfo,
            /* [in] */ LCID lcid,
            /* [out] */ ITypeInfo **ppTInfo);
        
        HRESULT ( STDMETHODCALLTYPE *GetIDsOfNames )( 
            _Parameter * This,
            /* [in] */ REFIID riid,
            /* [size_is][in] */ LPOLESTR *rgszNames,
            /* [range][in] */ UINT cNames,
            /* [in] */ LCID lcid,
            /* [size_is][out] */ DISPID *rgDispId);
        
        /* [local] */ HRESULT ( STDMETHODCALLTYPE *Invoke )( 
            _Parameter * This,
            /* [in] */ DISPID dispIdMember,
            /* [in] */ REFIID riid,
            /* [in] */ LCID lcid,
            /* [in] */ WORD wFlags,
            /* [out][in] */ DISPPARAMS *pDispParams,
            /* [out] */ VARIANT *pVarResult,
            /* [out] */ EXCEPINFO *pExcepInfo,
            /* [out] */ UINT *puArgErr);
        
        /* [propget][id] */ HRESULT ( __stdcall *get_Properties )( 
            _Parameter * This,
            /* [retval][out] */ Properties **ppvObject);
        
        /* [propget][id] */ HRESULT ( __stdcall *get_Name )( 
            _Parameter * This,
            /* [retval][out] */ BSTR *pbstr);
        
        /* [propput][id] */ HRESULT ( __stdcall *put_Name )( 
            _Parameter * This,
            /* [in] */ BSTR pbstr);
        
        /* [propget][id] */ HRESULT ( __stdcall *get_Value )( 
            _Parameter * This,
            /* [retval][out] */ VARIANT *pvar);
        
        /* [propput][id] */ HRESULT ( __stdcall *put_Value )( 
            _Parameter * This,
            /* [in] */ VARIANT pvar);
        
        /* [propget][id] */ HRESULT ( __stdcall *get_Type )( 
            _Parameter * This,
            /* [retval][out] */ DataTypeEnum *psDataType);
        
        /* [propput][id] */ HRESULT ( __stdcall *put_Type )( 
            _Parameter * This,
            /* [in] */ DataTypeEnum psDataType);
        
        /* [propput][id] */ HRESULT ( __stdcall *put_Direction )( 
            _Parameter * This,
            /* [in] */ ParameterDirectionEnum plParmDirection);
        
        /* [propget][id] */ HRESULT ( __stdcall *get_Direction )( 
            _Parameter * This,
            /* [retval][out] */ ParameterDirectionEnum *plParmDirection);
        
        /* [propput][id] */ HRESULT ( __stdcall *put_Precision )( 
            _Parameter * This,
            /* [in] */ unsigned char pbPrecision);
        
        /* [propget][id] */ HRESULT ( __stdcall *get_Precision )( 
            _Parameter * This,
            /* [retval][out] */ unsigned char *pbPrecision);
        
        /* [propput][id] */ HRESULT ( __stdcall *put_NumericScale )( 
            _Parameter * This,
            /* [in] */ unsigned char pbScale);
        
        /* [propget][id] */ HRESULT ( __stdcall *get_NumericScale )( 
            _Parameter * This,
            /* [retval][out] */ unsigned char *pbScale);
        
        /* [propput][id] */ HRESULT ( __stdcall *put_Size )( 
            _Parameter * This,
            /* [in] */ ADO_LONGPTR pl);
        
        /* [propget][id] */ HRESULT ( __stdcall *get_Size )( 
            _Parameter * This,
            /* [retval][out] */ ADO_LONGPTR *pl);
        
        /* [id] */ HRESULT ( __stdcall *AppendChunk )( 
            _Parameter * This,
            /* [in] */ VARIANT Val);
        
        /* [propget][id] */ HRESULT ( __stdcall *get_Attributes )( 
            _Parameter * This,
            /* [retval][out] */ long *plParmAttribs);
        
        /* [propput][id] */ HRESULT ( __stdcall *put_Attributes )( 
            _Parameter * This,
            /* [in] */ long plParmAttribs);
        
        END_INTERFACE
    } _ParameterVtbl;

    interface _Parameter
    {
        CONST_VTBL struct _ParameterVtbl *lpVtbl;
    };

    

#ifdef COBJMACROS


#define _Parameter_QueryInterface(This,riid,ppvObject)	\
    ( (This)->lpVtbl -> QueryInterface(This,riid,ppvObject) ) 

#define _Parameter_AddRef(This)	\
    ( (This)->lpVtbl -> AddRef(This) ) 

#define _Parameter_Release(This)	\
    ( (This)->lpVtbl -> Release(This) ) 


#define _Parameter_GetTypeInfoCount(This,pctinfo)	\
    ( (This)->lpVtbl -> GetTypeInfoCount(This,pctinfo) ) 

#define _Parameter_GetTypeInfo(This,iTInfo,lcid,ppTInfo)	\
    ( (This)->lpVtbl -> GetTypeInfo(This,iTInfo,lcid,ppTInfo) ) 

#define _Parameter_GetIDsOfNames(This,riid,rgszNames,cNames,lcid,rgDispId)	\
    ( (This)->lpVtbl -> GetIDsOfNames(This,riid,rgszNames,cNames,lcid,rgDispId) ) 

#define _Parameter_Invoke(This,dispIdMember,riid,lcid,wFlags,pDispParams,pVarResult,pExcepInfo,puArgErr)	\
    ( (This)->lpVtbl -> Invoke(This,dispIdMember,riid,lcid,wFlags,pDispParams,pVarResult,pExcepInfo,puArgErr) ) 


#define _Parameter_get_Properties(This,ppvObject)	\
    ( (This)->lpVtbl -> get_Properties(This,ppvObject) ) 


#define _Parameter_get_Name(This,pbstr)	\
    ( (This)->lpVtbl -> get_Name(This,pbstr) ) 

#define _Parameter_put_Name(This,pbstr)	\
    ( (This)->lpVtbl -> put_Name(This,pbstr) ) 

#define _Parameter_get_Value(This,pvar)	\
    ( (This)->lpVtbl -> get_Value(This,pvar) ) 

#define _Parameter_put_Value(This,pvar)	\
    ( (This)->lpVtbl -> put_Value(This,pvar) ) 

#define _Parameter_get_Type(This,psDataType)	\
    ( (This)->lpVtbl -> get_Type(This,psDataType) ) 

#define _Parameter_put_Type(This,psDataType)	\
    ( (This)->lpVtbl -> put_Type(This,psDataType) ) 

#define _Parameter_put_Direction(This,plParmDirection)	\
    ( (This)->lpVtbl -> put_Direction(This,plParmDirection) ) 

#define _Parameter_get_Direction(This,plParmDirection)	\
    ( (This)->lpVtbl -> get_Direction(This,plParmDirection) ) 

#define _Parameter_put_Precision(This,pbPrecision)	\
    ( (This)->lpVtbl -> put_Precision(This,pbPrecision) ) 

#define _Parameter_get_Precision(This,pbPrecision)	\
    ( (This)->lpVtbl -> get_Precision(This,pbPrecision) ) 

#define _Parameter_put_NumericScale(This,pbScale)	\
    ( (This)->lpVtbl -> put_NumericScale(This,pbScale) ) 

#define _Parameter_get_NumericScale(This,pbScale)	\
    ( (This)->lpVtbl -> get_NumericScale(This,pbScale) ) 

#define _Parameter_put_Size(This,pl)	\
    ( (This)->lpVtbl -> put_Size(This,pl) ) 

#define _Parameter_get_Size(This,pl)	\
    ( (This)->lpVtbl -> get_Size(This,pl) ) 

#define _Parameter_AppendChunk(This,Val)	\
    ( (This)->lpVtbl -> AppendChunk(This,Val) ) 

#define _Parameter_get_Attributes(This,plParmAttribs)	\
    ( (This)->lpVtbl -> get_Attributes(This,plParmAttribs) ) 

#define _Parameter_put_Attributes(This,plParmAttribs)	\
    ( (This)->lpVtbl -> put_Attributes(This,plParmAttribs) ) 

#endif /* COBJMACROS */


#endif 	/* C style interface */




#endif 	/* ___Parameter_INTERFACE_DEFINED__ */


#ifndef __Parameters_INTERFACE_DEFINED__
#define __Parameters_INTERFACE_DEFINED__

/* interface Parameters */
/* [object][oleautomation][nonextensible][dual][uuid] */ 


EXTERN_C const IID IID_Parameters;

#if defined(__cplusplus) && !defined(CINTERFACE)
    
    MIDL_INTERFACE("0000050D-0000-0010-8000-00AA006D2EA4")
    Parameters : public _DynaCollection
    {
    public:
        virtual /* [propget][id] */ HRESULT __stdcall get_Item( 
            /* [in] */ VARIANT Index,
            /* [retval][out] */ _Parameter **ppvObject) = 0;
        
    };
    
#else 	/* C style interface */

    typedef struct ParametersVtbl
    {
        BEGIN_INTERFACE
        
        HRESULT ( STDMETHODCALLTYPE *QueryInterface )( 
            Parameters * This,
            /* [in] */ REFIID riid,
            /* [annotation][iid_is][out] */ 
            __RPC__deref_out  void **ppvObject);
        
        ULONG ( STDMETHODCALLTYPE *AddRef )( 
            Parameters * This);
        
        ULONG ( STDMETHODCALLTYPE *Release )( 
            Parameters * This);
        
        HRESULT ( STDMETHODCALLTYPE *GetTypeInfoCount )( 
            Parameters * This,
            /* [out] */ UINT *pctinfo);
        
        HRESULT ( STDMETHODCALLTYPE *GetTypeInfo )( 
            Parameters * This,
            /* [in] */ UINT iTInfo,
            /* [in] */ LCID lcid,
            /* [out] */ ITypeInfo **ppTInfo);
        
        HRESULT ( STDMETHODCALLTYPE *GetIDsOfNames )( 
            Parameters * This,
            /* [in] */ REFIID riid,
            /* [size_is][in] */ LPOLESTR *rgszNames,
            /* [range][in] */ UINT cNames,
            /* [in] */ LCID lcid,
            /* [size_is][out] */ DISPID *rgDispId);
        
        /* [local] */ HRESULT ( STDMETHODCALLTYPE *Invoke )( 
            Parameters * This,
            /* [in] */ DISPID dispIdMember,
            /* [in] */ REFIID riid,
            /* [in] */ LCID lcid,
            /* [in] */ WORD wFlags,
            /* [out][in] */ DISPPARAMS *pDispParams,
            /* [out] */ VARIANT *pVarResult,
            /* [out] */ EXCEPINFO *pExcepInfo,
            /* [out] */ UINT *puArgErr);
        
        /* [propget][id] */ HRESULT ( __stdcall *get_Count )( 
            Parameters * This,
            /* [retval][out] */ long *c);
        
        /* [restricted][id] */ HRESULT ( __stdcall *_NewEnum )( 
            Parameters * This,
            /* [retval][out] */ IUnknown **ppvObject);
        
        /* [id] */ HRESULT ( __stdcall *Refresh )( 
            Parameters * This);
        
        /* [id] */ HRESULT ( __stdcall *Append )( 
            Parameters * This,
            /* [in] */ IDispatch *Object);
        
        /* [id] */ HRESULT ( __stdcall *Delete )( 
            Parameters * This,
            /* [in] */ VARIANT Index);
        
        /* [propget][id] */ HRESULT ( __stdcall *get_Item )( 
            Parameters * This,
            /* [in] */ VARIANT Index,
            /* [retval][out] */ _Parameter **ppvObject);
        
        END_INTERFACE
    } ParametersVtbl;

    interface Parameters
    {
        CONST_VTBL struct ParametersVtbl *lpVtbl;
    };

    

#ifdef COBJMACROS


#define Parameters_QueryInterface(This,riid,ppvObject)	\
    ( (This)->lpVtbl -> QueryInterface(This,riid,ppvObject) ) 

#define Parameters_AddRef(This)	\
    ( (This)->lpVtbl -> AddRef(This) ) 

#define Parameters_Release(This)	\
    ( (This)->lpVtbl -> Release(This) ) 


#define Parameters_GetTypeInfoCount(This,pctinfo)	\
    ( (This)->lpVtbl -> GetTypeInfoCount(This,pctinfo) ) 

#define Parameters_GetTypeInfo(This,iTInfo,lcid,ppTInfo)	\
    ( (This)->lpVtbl -> GetTypeInfo(This,iTInfo,lcid,ppTInfo) ) 

#define Parameters_GetIDsOfNames(This,riid,rgszNames,cNames,lcid,rgDispId)	\
    ( (This)->lpVtbl -> GetIDsOfNames(This,riid,rgszNames,cNames,lcid,rgDispId) ) 

#define Parameters_Invoke(This,dispIdMember,riid,lcid,wFlags,pDispParams,pVarResult,pExcepInfo,puArgErr)	\
    ( (This)->lpVtbl -> Invoke(This,dispIdMember,riid,lcid,wFlags,pDispParams,pVarResult,pExcepInfo,puArgErr) ) 


#define Parameters_get_Count(This,c)	\
    ( (This)->lpVtbl -> get_Count(This,c) ) 

#define Parameters__NewEnum(This,ppvObject)	\
    ( (This)->lpVtbl -> _NewEnum(This,ppvObject) ) 

#define Parameters_Refresh(This)	\
    ( (This)->lpVtbl -> Refresh(This) ) 


#define Parameters_Append(This,Object)	\
    ( (This)->lpVtbl -> Append(This,Object) ) 

#define Parameters_Delete(This,Index)	\
    ( (This)->lpVtbl -> Delete(This,Index) ) 


#define Parameters_get_Item(This,Index,ppvObject)	\
    ( (This)->lpVtbl -> get_Item(This,Index,ppvObject) ) 

#endif /* COBJMACROS */


#endif 	/* C style interface */




#endif 	/* __Parameters_INTERFACE_DEFINED__ */


#ifndef ___Record_INTERFACE_DEFINED__
#define ___Record_INTERFACE_DEFINED__

/* interface _Record */
/* [object][oleautomation][nonextensible][dual][uuid] */ 


EXTERN_C const IID IID__Record;

#if defined(__cplusplus) && !defined(CINTERFACE)
    
    MIDL_INTERFACE("00000562-0000-0010-8000-00AA006D2EA4")
    _Record : public _ADO
    {
    public:
        virtual /* [propget][id] */ HRESULT __stdcall get_ActiveConnection( 
            /* [retval][out] */ VARIANT *pvar) = 0;
        
        virtual /* [propput][id] */ HRESULT __stdcall put_ActiveConnection( 
            /* [in] */ BSTR bstrConn) = 0;
        
        virtual /* [propputref][id] */ HRESULT STDMETHODCALLTYPE putref_ActiveConnection( 
            /* [in] */ _Connection *Con) = 0;
        
        virtual /* [propget][id] */ HRESULT __stdcall get_State( 
            /* [retval][out] */ ObjectStateEnum *pState) = 0;
        
        virtual /* [propget][id] */ HRESULT __stdcall get_Source( 
            /* [retval][out] */ VARIANT *pvar) = 0;
        
        virtual /* [propput][id] */ HRESULT __stdcall put_Source( 
            /* [in] */ BSTR Source) = 0;
        
        virtual /* [propputref][id] */ HRESULT __stdcall putref_Source( 
            /* [in] */ IDispatch *Source) = 0;
        
        virtual /* [propget][id] */ HRESULT __stdcall get_Mode( 
            /* [retval][out] */ ConnectModeEnum *pMode) = 0;
        
        virtual /* [propput][id] */ HRESULT __stdcall put_Mode( 
            /* [in] */ ConnectModeEnum Mode) = 0;
        
        virtual /* [propget][id] */ HRESULT __stdcall get_ParentURL( 
            /* [retval][out] */ BSTR *pbstrParentURL) = 0;
        
        virtual /* [id] */ HRESULT __stdcall MoveRecord( 
            /* [defaultvalue][in] */ BSTR Source,
            /* [defaultvalue][in] */ BSTR Destination,
            /* [optional][in] */ BSTR UserName,
            /* [optional][in] */ BSTR Password,
            /* [defaultvalue][in] */ MoveRecordOptionsEnum Options,
            /* [optional][in] */ VARIANT_BOOL Async,
            /* [retval][out] */ BSTR *pbstrNewURL) = 0;
        
        virtual /* [id] */ HRESULT __stdcall CopyRecord( 
            /* [defaultvalue][in] */ BSTR Source,
            /* [defaultvalue][in] */ BSTR Destination,
            /* [optional][in] */ BSTR UserName,
            /* [optional][in] */ BSTR Password,
            /* [defaultvalue][in] */ CopyRecordOptionsEnum Options,
            /* [optional][in] */ VARIANT_BOOL Async,
            /* [retval][out] */ BSTR *pbstrNewURL) = 0;
        
        virtual /* [id] */ HRESULT __stdcall DeleteRecord( 
            /* [in] */ BSTR Source,
            /* [optional][in] */ VARIANT_BOOL Async) = 0;
        
        virtual /* [id] */ HRESULT __stdcall Open( 
            /* [optional][in] */ VARIANT Source,
            /* [optional][in] */ VARIANT ActiveConnection,
            /* [optional][in] */ ConnectModeEnum Mode,
            /* [defaultvalue][in] */ RecordCreateOptionsEnum CreateOptions,
            /* [defaultvalue][in] */ RecordOpenOptionsEnum Options,
            /* [optional][in] */ BSTR UserName,
            /* [optional][in] */ BSTR Password) = 0;
        
        virtual /* [id] */ HRESULT __stdcall Close( void) = 0;
        
        virtual /* [propget][id] */ HRESULT __stdcall get_Fields( 
            /* [retval][out] */ Fields **ppFlds) = 0;
        
        virtual /* [propget][id] */ HRESULT __stdcall get_RecordType( 
            /* [retval][out] */ RecordTypeEnum *pType) = 0;
        
        virtual /* [id] */ HRESULT __stdcall GetChildren( 
            /* [retval][out] */ _Recordset **ppRSet) = 0;
        
        virtual /* [id] */ HRESULT __stdcall Cancel( void) = 0;
        
    };
    
#else 	/* C style interface */

    typedef struct _RecordVtbl
    {
        BEGIN_INTERFACE
        
        HRESULT ( STDMETHODCALLTYPE *QueryInterface )( 
            _Record * This,
            /* [in] */ REFIID riid,
            /* [annotation][iid_is][out] */ 
            __RPC__deref_out  void **ppvObject);
        
        ULONG ( STDMETHODCALLTYPE *AddRef )( 
            _Record * This);
        
        ULONG ( STDMETHODCALLTYPE *Release )( 
            _Record * This);
        
        HRESULT ( STDMETHODCALLTYPE *GetTypeInfoCount )( 
            _Record * This,
            /* [out] */ UINT *pctinfo);
        
        HRESULT ( STDMETHODCALLTYPE *GetTypeInfo )( 
            _Record * This,
            /* [in] */ UINT iTInfo,
            /* [in] */ LCID lcid,
            /* [out] */ ITypeInfo **ppTInfo);
        
        HRESULT ( STDMETHODCALLTYPE *GetIDsOfNames )( 
            _Record * This,
            /* [in] */ REFIID riid,
            /* [size_is][in] */ LPOLESTR *rgszNames,
            /* [range][in] */ UINT cNames,
            /* [in] */ LCID lcid,
            /* [size_is][out] */ DISPID *rgDispId);
        
        /* [local] */ HRESULT ( STDMETHODCALLTYPE *Invoke )( 
            _Record * This,
            /* [in] */ DISPID dispIdMember,
            /* [in] */ REFIID riid,
            /* [in] */ LCID lcid,
            /* [in] */ WORD wFlags,
            /* [out][in] */ DISPPARAMS *pDispParams,
            /* [out] */ VARIANT *pVarResult,
            /* [out] */ EXCEPINFO *pExcepInfo,
            /* [out] */ UINT *puArgErr);
        
        /* [propget][id] */ HRESULT ( __stdcall *get_Properties )( 
            _Record * This,
            /* [retval][out] */ Properties **ppvObject);
        
        /* [propget][id] */ HRESULT ( __stdcall *get_ActiveConnection )( 
            _Record * This,
            /* [retval][out] */ VARIANT *pvar);
        
        /* [propput][id] */ HRESULT ( __stdcall *put_ActiveConnection )( 
            _Record * This,
            /* [in] */ BSTR bstrConn);
        
        /* [propputref][id] */ HRESULT ( STDMETHODCALLTYPE *putref_ActiveConnection )( 
            _Record * This,
            /* [in] */ _Connection *Con);
        
        /* [propget][id] */ HRESULT ( __stdcall *get_State )( 
            _Record * This,
            /* [retval][out] */ ObjectStateEnum *pState);
        
        /* [propget][id] */ HRESULT ( __stdcall *get_Source )( 
            _Record * This,
            /* [retval][out] */ VARIANT *pvar);
        
        /* [propput][id] */ HRESULT ( __stdcall *put_Source )( 
            _Record * This,
            /* [in] */ BSTR Source);
        
        /* [propputref][id] */ HRESULT ( __stdcall *putref_Source )( 
            _Record * This,
            /* [in] */ IDispatch *Source);
        
        /* [propget][id] */ HRESULT ( __stdcall *get_Mode )( 
            _Record * This,
            /* [retval][out] */ ConnectModeEnum *pMode);
        
        /* [propput][id] */ HRESULT ( __stdcall *put_Mode )( 
            _Record * This,
            /* [in] */ ConnectModeEnum Mode);
        
        /* [propget][id] */ HRESULT ( __stdcall *get_ParentURL )( 
            _Record * This,
            /* [retval][out] */ BSTR *pbstrParentURL);
        
        /* [id] */ HRESULT ( __stdcall *MoveRecord )( 
            _Record * This,
            /* [defaultvalue][in] */ BSTR Source,
            /* [defaultvalue][in] */ BSTR Destination,
            /* [optional][in] */ BSTR UserName,
            /* [optional][in] */ BSTR Password,
            /* [defaultvalue][in] */ MoveRecordOptionsEnum Options,
            /* [optional][in] */ VARIANT_BOOL Async,
            /* [retval][out] */ BSTR *pbstrNewURL);
        
        /* [id] */ HRESULT ( __stdcall *CopyRecord )( 
            _Record * This,
            /* [defaultvalue][in] */ BSTR Source,
            /* [defaultvalue][in] */ BSTR Destination,
            /* [optional][in] */ BSTR UserName,
            /* [optional][in] */ BSTR Password,
            /* [defaultvalue][in] */ CopyRecordOptionsEnum Options,
            /* [optional][in] */ VARIANT_BOOL Async,
            /* [retval][out] */ BSTR *pbstrNewURL);
        
        /* [id] */ HRESULT ( __stdcall *DeleteRecord )( 
            _Record * This,
            /* [in] */ BSTR Source,
            /* [optional][in] */ VARIANT_BOOL Async);
        
        /* [id] */ HRESULT ( __stdcall *Open )( 
            _Record * This,
            /* [optional][in] */ VARIANT Source,
            /* [optional][in] */ VARIANT ActiveConnection,
            /* [optional][in] */ ConnectModeEnum Mode,
            /* [defaultvalue][in] */ RecordCreateOptionsEnum CreateOptions,
            /* [defaultvalue][in] */ RecordOpenOptionsEnum Options,
            /* [optional][in] */ BSTR UserName,
            /* [optional][in] */ BSTR Password);
        
        /* [id] */ HRESULT ( __stdcall *Close )( 
            _Record * This);
        
        /* [propget][id] */ HRESULT ( __stdcall *get_Fields )( 
            _Record * This,
            /* [retval][out] */ Fields **ppFlds);
        
        /* [propget][id] */ HRESULT ( __stdcall *get_RecordType )( 
            _Record * This,
            /* [retval][out] */ RecordTypeEnum *pType);
        
        /* [id] */ HRESULT ( __stdcall *GetChildren )( 
            _Record * This,
            /* [retval][out] */ _Recordset **ppRSet);
        
        /* [id] */ HRESULT ( __stdcall *Cancel )( 
            _Record * This);
        
        END_INTERFACE
    } _RecordVtbl;

    interface _Record
    {
        CONST_VTBL struct _RecordVtbl *lpVtbl;
    };

    

#ifdef COBJMACROS


#define _Record_QueryInterface(This,riid,ppvObject)	\
    ( (This)->lpVtbl -> QueryInterface(This,riid,ppvObject) ) 

#define _Record_AddRef(This)	\
    ( (This)->lpVtbl -> AddRef(This) ) 

#define _Record_Release(This)	\
    ( (This)->lpVtbl -> Release(This) ) 


#define _Record_GetTypeInfoCount(This,pctinfo)	\
    ( (This)->lpVtbl -> GetTypeInfoCount(This,pctinfo) ) 

#define _Record_GetTypeInfo(This,iTInfo,lcid,ppTInfo)	\
    ( (This)->lpVtbl -> GetTypeInfo(This,iTInfo,lcid,ppTInfo) ) 

#define _Record_GetIDsOfNames(This,riid,rgszNames,cNames,lcid,rgDispId)	\
    ( (This)->lpVtbl -> GetIDsOfNames(This,riid,rgszNames,cNames,lcid,rgDispId) ) 

#define _Record_Invoke(This,dispIdMember,riid,lcid,wFlags,pDispParams,pVarResult,pExcepInfo,puArgErr)	\
    ( (This)->lpVtbl -> Invoke(This,dispIdMember,riid,lcid,wFlags,pDispParams,pVarResult,pExcepInfo,puArgErr) ) 


#define _Record_get_Properties(This,ppvObject)	\
    ( (This)->lpVtbl -> get_Properties(This,ppvObject) ) 


#define _Record_get_ActiveConnection(This,pvar)	\
    ( (This)->lpVtbl -> get_ActiveConnection(This,pvar) ) 

#define _Record_put_ActiveConnection(This,bstrConn)	\
    ( (This)->lpVtbl -> put_ActiveConnection(This,bstrConn) ) 

#define _Record_putref_ActiveConnection(This,Con)	\
    ( (This)->lpVtbl -> putref_ActiveConnection(This,Con) ) 

#define _Record_get_State(This,pState)	\
    ( (This)->lpVtbl -> get_State(This,pState) ) 

#define _Record_get_Source(This,pvar)	\
    ( (This)->lpVtbl -> get_Source(This,pvar) ) 

#define _Record_put_Source(This,Source)	\
    ( (This)->lpVtbl -> put_Source(This,Source) ) 

#define _Record_putref_Source(This,Source)	\
    ( (This)->lpVtbl -> putref_Source(This,Source) ) 

#define _Record_get_Mode(This,pMode)	\
    ( (This)->lpVtbl -> get_Mode(This,pMode) ) 

#define _Record_put_Mode(This,Mode)	\
    ( (This)->lpVtbl -> put_Mode(This,Mode) ) 

#define _Record_get_ParentURL(This,pbstrParentURL)	\
    ( (This)->lpVtbl -> get_ParentURL(This,pbstrParentURL) ) 

#define _Record_MoveRecord(This,Source,Destination,UserName,Password,Options,Async,pbstrNewURL)	\
    ( (This)->lpVtbl -> MoveRecord(This,Source,Destination,UserName,Password,Options,Async,pbstrNewURL) ) 

#define _Record_CopyRecord(This,Source,Destination,UserName,Password,Options,Async,pbstrNewURL)	\
    ( (This)->lpVtbl -> CopyRecord(This,Source,Destination,UserName,Password,Options,Async,pbstrNewURL) ) 

#define _Record_DeleteRecord(This,Source,Async)	\
    ( (This)->lpVtbl -> DeleteRecord(This,Source,Async) ) 

#define _Record_Open(This,Source,ActiveConnection,Mode,CreateOptions,Options,UserName,Password)	\
    ( (This)->lpVtbl -> Open(This,Source,ActiveConnection,Mode,CreateOptions,Options,UserName,Password) ) 

#define _Record_Close(This)	\
    ( (This)->lpVtbl -> Close(This) ) 

#define _Record_get_Fields(This,ppFlds)	\
    ( (This)->lpVtbl -> get_Fields(This,ppFlds) ) 

#define _Record_get_RecordType(This,pType)	\
    ( (This)->lpVtbl -> get_RecordType(This,pType) ) 

#define _Record_GetChildren(This,ppRSet)	\
    ( (This)->lpVtbl -> GetChildren(This,ppRSet) ) 

#define _Record_Cancel(This)	\
    ( (This)->lpVtbl -> Cancel(This) ) 

#endif /* COBJMACROS */


#endif 	/* C style interface */




#endif 	/* ___Record_INTERFACE_DEFINED__ */


#ifndef ___Stream_INTERFACE_DEFINED__
#define ___Stream_INTERFACE_DEFINED__

/* interface _Stream */
/* [object][oleautomation][nonextensible][dual][uuid] */ 


EXTERN_C const IID IID__Stream;

#if defined(__cplusplus) && !defined(CINTERFACE)
    
    MIDL_INTERFACE("00000565-0000-0010-8000-00AA006D2EA4")
    _Stream : public IDispatch
    {
    public:
        virtual /* [propget][id] */ HRESULT __stdcall get_Size( 
            /* [retval][out] */ ADO_LONGPTR *pSize) = 0;
        
        virtual /* [propget][id] */ HRESULT __stdcall get_EOS( 
            /* [retval][out] */ VARIANT_BOOL *pEOS) = 0;
        
        virtual /* [propget][id] */ HRESULT __stdcall get_Position( 
            /* [retval][out] */ ADO_LONGPTR *pPos) = 0;
        
        virtual /* [propput][id] */ HRESULT __stdcall put_Position( 
            /* [in] */ ADO_LONGPTR Position) = 0;
        
        virtual /* [propget][id] */ HRESULT __stdcall get_Type( 
            /* [retval][out] */ StreamTypeEnum *pType) = 0;
        
        virtual /* [propput][id] */ HRESULT __stdcall put_Type( 
            /* [in] */ StreamTypeEnum Type) = 0;
        
        virtual /* [propget][id] */ HRESULT __stdcall get_LineSeparator( 
            /* [retval][out] */ LineSeparatorEnum *pLS) = 0;
        
        virtual /* [propput][id] */ HRESULT __stdcall put_LineSeparator( 
            /* [in] */ LineSeparatorEnum LineSeparator) = 0;
        
        virtual /* [propget][id] */ HRESULT __stdcall get_State( 
            /* [retval][out] */ ObjectStateEnum *pState) = 0;
        
        virtual /* [propget][id] */ HRESULT __stdcall get_Mode( 
            /* [retval][out] */ ConnectModeEnum *pMode) = 0;
        
        virtual /* [propput][id] */ HRESULT __stdcall put_Mode( 
            /* [in] */ ConnectModeEnum Mode) = 0;
        
        virtual /* [propget][id] */ HRESULT STDMETHODCALLTYPE get_Charset( 
            /* [retval][out] */ BSTR *pbstrCharset) = 0;
        
        virtual /* [propput][id] */ HRESULT STDMETHODCALLTYPE put_Charset( 
            /* [in] */ BSTR Charset) = 0;
        
        virtual /* [id] */ HRESULT __stdcall Read( 
            /* [defaultvalue][in] */ long NumBytes,
            /* [retval][out] */ VARIANT *pVal) = 0;
        
        virtual /* [id] */ HRESULT __stdcall Open( 
            /* [optional][in] */ VARIANT Source,
            /* [defaultvalue][in] */ ConnectModeEnum Mode,
            /* [defaultvalue][in] */ StreamOpenOptionsEnum Options,
            /* [optional][in] */ BSTR UserName,
            /* [optional][in] */ BSTR Password) = 0;
        
        virtual /* [id] */ HRESULT __stdcall Close( void) = 0;
        
        virtual /* [id] */ HRESULT __stdcall SkipLine( void) = 0;
        
        virtual /* [id] */ HRESULT __stdcall Write( 
            /* [in] */ VARIANT Buffer) = 0;
        
        virtual /* [id] */ HRESULT __stdcall SetEOS( void) = 0;
        
        virtual /* [id] */ HRESULT __stdcall CopyTo( 
            /* [in] */ _Stream *DestStream,
            /* [defaultvalue][in] */ ADO_LONGPTR CharNumber = -1) = 0;
        
        virtual /* [id] */ HRESULT __stdcall Flush( void) = 0;
        
        virtual /* [id] */ HRESULT __stdcall SaveToFile( 
            /* [in] */ BSTR FileName,
            /* [defaultvalue][in] */ SaveOptionsEnum Options = adSaveCreateNotExist) = 0;
        
        virtual /* [id] */ HRESULT __stdcall LoadFromFile( 
            /* [in] */ BSTR FileName) = 0;
        
        virtual /* [id] */ HRESULT __stdcall ReadText( 
            /* [defaultvalue][in] */ long NumChars,
            /* [retval][out] */ BSTR *pbstr) = 0;
        
        virtual /* [id] */ HRESULT __stdcall WriteText( 
            /* [in] */ BSTR Data,
            /* [defaultvalue][in] */ StreamWriteEnum Options = adWriteChar) = 0;
        
        virtual /* [id] */ HRESULT __stdcall Cancel( void) = 0;
        
    };
    
#else 	/* C style interface */

    typedef struct _StreamVtbl
    {
        BEGIN_INTERFACE
        
        HRESULT ( STDMETHODCALLTYPE *QueryInterface )( 
            _Stream * This,
            /* [in] */ REFIID riid,
            /* [annotation][iid_is][out] */ 
            __RPC__deref_out  void **ppvObject);
        
        ULONG ( STDMETHODCALLTYPE *AddRef )( 
            _Stream * This);
        
        ULONG ( STDMETHODCALLTYPE *Release )( 
            _Stream * This);
        
        HRESULT ( STDMETHODCALLTYPE *GetTypeInfoCount )( 
            _Stream * This,
            /* [out] */ UINT *pctinfo);
        
        HRESULT ( STDMETHODCALLTYPE *GetTypeInfo )( 
            _Stream * This,
            /* [in] */ UINT iTInfo,
            /* [in] */ LCID lcid,
            /* [out] */ ITypeInfo **ppTInfo);
        
        HRESULT ( STDMETHODCALLTYPE *GetIDsOfNames )( 
            _Stream * This,
            /* [in] */ REFIID riid,
            /* [size_is][in] */ LPOLESTR *rgszNames,
            /* [range][in] */ UINT cNames,
            /* [in] */ LCID lcid,
            /* [size_is][out] */ DISPID *rgDispId);
        
        /* [local] */ HRESULT ( STDMETHODCALLTYPE *Invoke )( 
            _Stream * This,
            /* [in] */ DISPID dispIdMember,
            /* [in] */ REFIID riid,
            /* [in] */ LCID lcid,
            /* [in] */ WORD wFlags,
            /* [out][in] */ DISPPARAMS *pDispParams,
            /* [out] */ VARIANT *pVarResult,
            /* [out] */ EXCEPINFO *pExcepInfo,
            /* [out] */ UINT *puArgErr);
        
        /* [propget][id] */ HRESULT ( __stdcall *get_Size )( 
            _Stream * This,
            /* [retval][out] */ ADO_LONGPTR *pSize);
        
        /* [propget][id] */ HRESULT ( __stdcall *get_EOS )( 
            _Stream * This,
            /* [retval][out] */ VARIANT_BOOL *pEOS);
        
        /* [propget][id] */ HRESULT ( __stdcall *get_Position )( 
            _Stream * This,
            /* [retval][out] */ ADO_LONGPTR *pPos);
        
        /* [propput][id] */ HRESULT ( __stdcall *put_Position )( 
            _Stream * This,
            /* [in] */ ADO_LONGPTR Position);
        
        /* [propget][id] */ HRESULT ( __stdcall *get_Type )( 
            _Stream * This,
            /* [retval][out] */ StreamTypeEnum *pType);
        
        /* [propput][id] */ HRESULT ( __stdcall *put_Type )( 
            _Stream * This,
            /* [in] */ StreamTypeEnum Type);
        
        /* [propget][id] */ HRESULT ( __stdcall *get_LineSeparator )( 
            _Stream * This,
            /* [retval][out] */ LineSeparatorEnum *pLS);
        
        /* [propput][id] */ HRESULT ( __stdcall *put_LineSeparator )( 
            _Stream * This,
            /* [in] */ LineSeparatorEnum LineSeparator);
        
        /* [propget][id] */ HRESULT ( __stdcall *get_State )( 
            _Stream * This,
            /* [retval][out] */ ObjectStateEnum *pState);
        
        /* [propget][id] */ HRESULT ( __stdcall *get_Mode )( 
            _Stream * This,
            /* [retval][out] */ ConnectModeEnum *pMode);
        
        /* [propput][id] */ HRESULT ( __stdcall *put_Mode )( 
            _Stream * This,
            /* [in] */ ConnectModeEnum Mode);
        
        /* [propget][id] */ HRESULT ( STDMETHODCALLTYPE *get_Charset )( 
            _Stream * This,
            /* [retval][out] */ BSTR *pbstrCharset);
        
        /* [propput][id] */ HRESULT ( STDMETHODCALLTYPE *put_Charset )( 
            _Stream * This,
            /* [in] */ BSTR Charset);
        
        /* [id] */ HRESULT ( __stdcall *Read )( 
            _Stream * This,
            /* [defaultvalue][in] */ long NumBytes,
            /* [retval][out] */ VARIANT *pVal);
        
        /* [id] */ HRESULT ( __stdcall *Open )( 
            _Stream * This,
            /* [optional][in] */ VARIANT Source,
            /* [defaultvalue][in] */ ConnectModeEnum Mode,
            /* [defaultvalue][in] */ StreamOpenOptionsEnum Options,
            /* [optional][in] */ BSTR UserName,
            /* [optional][in] */ BSTR Password);
        
        /* [id] */ HRESULT ( __stdcall *Close )( 
            _Stream * This);
        
        /* [id] */ HRESULT ( __stdcall *SkipLine )( 
            _Stream * This);
        
        /* [id] */ HRESULT ( __stdcall *Write )( 
            _Stream * This,
            /* [in] */ VARIANT Buffer);
        
        /* [id] */ HRESULT ( __stdcall *SetEOS )( 
            _Stream * This);
        
        /* [id] */ HRESULT ( __stdcall *CopyTo )( 
            _Stream * This,
            /* [in] */ _Stream *DestStream,
            /* [defaultvalue][in] */ ADO_LONGPTR CharNumber);
        
        /* [id] */ HRESULT ( __stdcall *Flush )( 
            _Stream * This);
        
        /* [id] */ HRESULT ( __stdcall *SaveToFile )( 
            _Stream * This,
            /* [in] */ BSTR FileName,
            /* [defaultvalue][in] */ SaveOptionsEnum Options);
        
        /* [id] */ HRESULT ( __stdcall *LoadFromFile )( 
            _Stream * This,
            /* [in] */ BSTR FileName);
        
        /* [id] */ HRESULT ( __stdcall *ReadText )( 
            _Stream * This,
            /* [defaultvalue][in] */ long NumChars,
            /* [retval][out] */ BSTR *pbstr);
        
        /* [id] */ HRESULT ( __stdcall *WriteText )( 
            _Stream * This,
            /* [in] */ BSTR Data,
            /* [defaultvalue][in] */ StreamWriteEnum Options);
        
        /* [id] */ HRESULT ( __stdcall *Cancel )( 
            _Stream * This);
        
        END_INTERFACE
    } _StreamVtbl;

    interface _Stream
    {
        CONST_VTBL struct _StreamVtbl *lpVtbl;
    };

    

#ifdef COBJMACROS


#define _Stream_QueryInterface(This,riid,ppvObject)	\
    ( (This)->lpVtbl -> QueryInterface(This,riid,ppvObject) ) 

#define _Stream_AddRef(This)	\
    ( (This)->lpVtbl -> AddRef(This) ) 

#define _Stream_Release(This)	\
    ( (This)->lpVtbl -> Release(This) ) 


#define _Stream_GetTypeInfoCount(This,pctinfo)	\
    ( (This)->lpVtbl -> GetTypeInfoCount(This,pctinfo) ) 

#define _Stream_GetTypeInfo(This,iTInfo,lcid,ppTInfo)	\
    ( (This)->lpVtbl -> GetTypeInfo(This,iTInfo,lcid,ppTInfo) ) 

#define _Stream_GetIDsOfNames(This,riid,rgszNames,cNames,lcid,rgDispId)	\
    ( (This)->lpVtbl -> GetIDsOfNames(This,riid,rgszNames,cNames,lcid,rgDispId) ) 

#define _Stream_Invoke(This,dispIdMember,riid,lcid,wFlags,pDispParams,pVarResult,pExcepInfo,puArgErr)	\
    ( (This)->lpVtbl -> Invoke(This,dispIdMember,riid,lcid,wFlags,pDispParams,pVarResult,pExcepInfo,puArgErr) ) 


#define _Stream_get_Size(This,pSize)	\
    ( (This)->lpVtbl -> get_Size(This,pSize) ) 

#define _Stream_get_EOS(This,pEOS)	\
    ( (This)->lpVtbl -> get_EOS(This,pEOS) ) 

#define _Stream_get_Position(This,pPos)	\
    ( (This)->lpVtbl -> get_Position(This,pPos) ) 

#define _Stream_put_Position(This,Position)	\
    ( (This)->lpVtbl -> put_Position(This,Position) ) 

#define _Stream_get_Type(This,pType)	\
    ( (This)->lpVtbl -> get_Type(This,pType) ) 

#define _Stream_put_Type(This,Type)	\
    ( (This)->lpVtbl -> put_Type(This,Type) ) 

#define _Stream_get_LineSeparator(This,pLS)	\
    ( (This)->lpVtbl -> get_LineSeparator(This,pLS) ) 

#define _Stream_put_LineSeparator(This,LineSeparator)	\
    ( (This)->lpVtbl -> put_LineSeparator(This,LineSeparator) ) 

#define _Stream_get_State(This,pState)	\
    ( (This)->lpVtbl -> get_State(This,pState) ) 

#define _Stream_get_Mode(This,pMode)	\
    ( (This)->lpVtbl -> get_Mode(This,pMode) ) 

#define _Stream_put_Mode(This,Mode)	\
    ( (This)->lpVtbl -> put_Mode(This,Mode) ) 

#define _Stream_get_Charset(This,pbstrCharset)	\
    ( (This)->lpVtbl -> get_Charset(This,pbstrCharset) ) 

#define _Stream_put_Charset(This,Charset)	\
    ( (This)->lpVtbl -> put_Charset(This,Charset) ) 

#define _Stream_Read(This,NumBytes,pVal)	\
    ( (This)->lpVtbl -> Read(This,NumBytes,pVal) ) 

#define _Stream_Open(This,Source,Mode,Options,UserName,Password)	\
    ( (This)->lpVtbl -> Open(This,Source,Mode,Options,UserName,Password) ) 

#define _Stream_Close(This)	\
    ( (This)->lpVtbl -> Close(This) ) 

#define _Stream_SkipLine(This)	\
    ( (This)->lpVtbl -> SkipLine(This) ) 

#define _Stream_Write(This,Buffer)	\
    ( (This)->lpVtbl -> Write(This,Buffer) ) 

#define _Stream_SetEOS(This)	\
    ( (This)->lpVtbl -> SetEOS(This) ) 

#define _Stream_CopyTo(This,DestStream,CharNumber)	\
    ( (This)->lpVtbl -> CopyTo(This,DestStream,CharNumber) ) 

#define _Stream_Flush(This)	\
    ( (This)->lpVtbl -> Flush(This) ) 

#define _Stream_SaveToFile(This,FileName,Options)	\
    ( (This)->lpVtbl -> SaveToFile(This,FileName,Options) ) 

#define _Stream_LoadFromFile(This,FileName)	\
    ( (This)->lpVtbl -> LoadFromFile(This,FileName) ) 

#define _Stream_ReadText(This,NumChars,pbstr)	\
    ( (This)->lpVtbl -> ReadText(This,NumChars,pbstr) ) 

#define _Stream_WriteText(This,Data,Options)	\
    ( (This)->lpVtbl -> WriteText(This,Data,Options) ) 

#define _Stream_Cancel(This)	\
    ( (This)->lpVtbl -> Cancel(This) ) 

#endif /* COBJMACROS */


#endif 	/* C style interface */




#endif 	/* ___Stream_INTERFACE_DEFINED__ */


#ifndef __ADODebugging_INTERFACE_DEFINED__
#define __ADODebugging_INTERFACE_DEFINED__

/* interface ADODebugging */
/* [object][hidden][uuid] */ 


EXTERN_C const IID IID_ADODebugging;

#if defined(__cplusplus) && !defined(CINTERFACE)
    
    MIDL_INTERFACE("00000538-0000-0010-8000-00AA006D2EA4")
    ADODebugging : public IUnknown
    {
    public:
        virtual HRESULT __stdcall IsGlobalDebugMode( 
            VARIANT_BOOL *pfDebuggingOn) = 0;
        
        virtual HRESULT __stdcall SetGlobalDebugMode( 
            IUnknown *pDebugger,
            VARIANT_BOOL fDebuggingOn) = 0;
        
    };
    
#else 	/* C style interface */

    typedef struct ADODebuggingVtbl
    {
        BEGIN_INTERFACE
        
        HRESULT ( STDMETHODCALLTYPE *QueryInterface )( 
            ADODebugging * This,
            /* [in] */ REFIID riid,
            /* [annotation][iid_is][out] */ 
            __RPC__deref_out  void **ppvObject);
        
        ULONG ( STDMETHODCALLTYPE *AddRef )( 
            ADODebugging * This);
        
        ULONG ( STDMETHODCALLTYPE *Release )( 
            ADODebugging * This);
        
        HRESULT ( __stdcall *IsGlobalDebugMode )( 
            ADODebugging * This,
            VARIANT_BOOL *pfDebuggingOn);
        
        HRESULT ( __stdcall *SetGlobalDebugMode )( 
            ADODebugging * This,
            IUnknown *pDebugger,
            VARIANT_BOOL fDebuggingOn);
        
        END_INTERFACE
    } ADODebuggingVtbl;

    interface ADODebugging
    {
        CONST_VTBL struct ADODebuggingVtbl *lpVtbl;
    };

    

#ifdef COBJMACROS


#define ADODebugging_QueryInterface(This,riid,ppvObject)	\
    ( (This)->lpVtbl -> QueryInterface(This,riid,ppvObject) ) 

#define ADODebugging_AddRef(This)	\
    ( (This)->lpVtbl -> AddRef(This) ) 

#define ADODebugging_Release(This)	\
    ( (This)->lpVtbl -> Release(This) ) 


#define ADODebugging_IsGlobalDebugMode(This,pfDebuggingOn)	\
    ( (This)->lpVtbl -> IsGlobalDebugMode(This,pfDebuggingOn) ) 

#define ADODebugging_SetGlobalDebugMode(This,pDebugger,fDebuggingOn)	\
    ( (This)->lpVtbl -> SetGlobalDebugMode(This,pDebugger,fDebuggingOn) ) 

#endif /* COBJMACROS */


#endif 	/* C style interface */




#endif 	/* __ADODebugging_INTERFACE_DEFINED__ */


#ifndef __ConnectionEventsVt_INTERFACE_DEFINED__
#define __ConnectionEventsVt_INTERFACE_DEFINED__

/* interface ConnectionEventsVt */
/* [object][hidden][uuid] */ 


EXTERN_C const IID IID_ConnectionEventsVt;

#if defined(__cplusplus) && !defined(CINTERFACE)
    
    MIDL_INTERFACE("00000402-0000-0010-8000-00AA006D2EA4")
    ConnectionEventsVt : public IUnknown
    {
    public:
        virtual HRESULT __stdcall InfoMessage( 
            /* [in] */ Error *pError,
            /* [out][in] */ EventStatusEnum *adStatus,
            /* [in] */ _Connection *pConnection) = 0;
        
        virtual HRESULT __stdcall BeginTransComplete( 
            /* [in] */ long TransactionLevel,
            /* [in] */ Error *pError,
            /* [out][in] */ EventStatusEnum *adStatus,
            /* [in] */ _Connection *pConnection) = 0;
        
        virtual HRESULT __stdcall CommitTransComplete( 
            /* [in] */ Error *pError,
            /* [out][in] */ EventStatusEnum *adStatus,
            /* [in] */ _Connection *pConnection) = 0;
        
        virtual HRESULT __stdcall RollbackTransComplete( 
            /* [in] */ Error *pError,
            /* [out][in] */ EventStatusEnum *adStatus,
            /* [in] */ _Connection *pConnection) = 0;
        
        virtual HRESULT __stdcall WillExecute( 
            /* [out][in] */ BSTR *Source,
            /* [out][in] */ CursorTypeEnum *CursorType,
            /* [out][in] */ LockTypeEnum *LockType,
            /* [out][in] */ long *Options,
            /* [out][in] */ EventStatusEnum *adStatus,
            /* [in] */ _Command *pCommand,
            /* [in] */ _Recordset *pRecordset,
            /* [in] */ _Connection *pConnection) = 0;
        
        virtual HRESULT __stdcall ExecuteComplete( 
            /* [in] */ long RecordsAffected,
            /* [in] */ Error *pError,
            /* [out][in] */ EventStatusEnum *adStatus,
            /* [in] */ _Command *pCommand,
            /* [in] */ _Recordset *pRecordset,
            /* [in] */ _Connection *pConnection) = 0;
        
        virtual HRESULT __stdcall WillConnect( 
            /* [out][in] */ BSTR *ConnectionString,
            /* [out][in] */ BSTR *UserID,
            /* [out][in] */ BSTR *Password,
            /* [out][in] */ long *Options,
            /* [out][in] */ EventStatusEnum *adStatus,
            /* [in] */ _Connection *pConnection) = 0;
        
        virtual HRESULT __stdcall ConnectComplete( 
            /* [in] */ Error *pError,
            /* [out][in] */ EventStatusEnum *adStatus,
            /* [in] */ _Connection *pConnection) = 0;
        
        virtual HRESULT __stdcall Disconnect( 
            /* [out][in] */ EventStatusEnum *adStatus,
            /* [in] */ _Connection *pConnection) = 0;
        
    };
    
#else 	/* C style interface */

    typedef struct ConnectionEventsVtVtbl
    {
        BEGIN_INTERFACE
        
        HRESULT ( STDMETHODCALLTYPE *QueryInterface )( 
            ConnectionEventsVt * This,
            /* [in] */ REFIID riid,
            /* [annotation][iid_is][out] */ 
            __RPC__deref_out  void **ppvObject);
        
        ULONG ( STDMETHODCALLTYPE *AddRef )( 
            ConnectionEventsVt * This);
        
        ULONG ( STDMETHODCALLTYPE *Release )( 
            ConnectionEventsVt * This);
        
        HRESULT ( __stdcall *InfoMessage )( 
            ConnectionEventsVt * This,
            /* [in] */ Error *pError,
            /* [out][in] */ EventStatusEnum *adStatus,
            /* [in] */ _Connection *pConnection);
        
        HRESULT ( __stdcall *BeginTransComplete )( 
            ConnectionEventsVt * This,
            /* [in] */ long TransactionLevel,
            /* [in] */ Error *pError,
            /* [out][in] */ EventStatusEnum *adStatus,
            /* [in] */ _Connection *pConnection);
        
        HRESULT ( __stdcall *CommitTransComplete )( 
            ConnectionEventsVt * This,
            /* [in] */ Error *pError,
            /* [out][in] */ EventStatusEnum *adStatus,
            /* [in] */ _Connection *pConnection);
        
        HRESULT ( __stdcall *RollbackTransComplete )( 
            ConnectionEventsVt * This,
            /* [in] */ Error *pError,
            /* [out][in] */ EventStatusEnum *adStatus,
            /* [in] */ _Connection *pConnection);
        
        HRESULT ( __stdcall *WillExecute )( 
            ConnectionEventsVt * This,
            /* [out][in] */ BSTR *Source,
            /* [out][in] */ CursorTypeEnum *CursorType,
            /* [out][in] */ LockTypeEnum *LockType,
            /* [out][in] */ long *Options,
            /* [out][in] */ EventStatusEnum *adStatus,
            /* [in] */ _Command *pCommand,
            /* [in] */ _Recordset *pRecordset,
            /* [in] */ _Connection *pConnection);
        
        HRESULT ( __stdcall *ExecuteComplete )( 
            ConnectionEventsVt * This,
            /* [in] */ long RecordsAffected,
            /* [in] */ Error *pError,
            /* [out][in] */ EventStatusEnum *adStatus,
            /* [in] */ _Command *pCommand,
            /* [in] */ _Recordset *pRecordset,
            /* [in] */ _Connection *pConnection);
        
        HRESULT ( __stdcall *WillConnect )( 
            ConnectionEventsVt * This,
            /* [out][in] */ BSTR *ConnectionString,
            /* [out][in] */ BSTR *UserID,
            /* [out][in] */ BSTR *Password,
            /* [out][in] */ long *Options,
            /* [out][in] */ EventStatusEnum *adStatus,
            /* [in] */ _Connection *pConnection);
        
        HRESULT ( __stdcall *ConnectComplete )( 
            ConnectionEventsVt * This,
            /* [in] */ Error *pError,
            /* [out][in] */ EventStatusEnum *adStatus,
            /* [in] */ _Connection *pConnection);
        
        HRESULT ( __stdcall *Disconnect )( 
            ConnectionEventsVt * This,
            /* [out][in] */ EventStatusEnum *adStatus,
            /* [in] */ _Connection *pConnection);
        
        END_INTERFACE
    } ConnectionEventsVtVtbl;

    interface ConnectionEventsVt
    {
        CONST_VTBL struct ConnectionEventsVtVtbl *lpVtbl;
    };

    

#ifdef COBJMACROS


#define ConnectionEventsVt_QueryInterface(This,riid,ppvObject)	\
    ( (This)->lpVtbl -> QueryInterface(This,riid,ppvObject) ) 

#define ConnectionEventsVt_AddRef(This)	\
    ( (This)->lpVtbl -> AddRef(This) ) 

#define ConnectionEventsVt_Release(This)	\
    ( (This)->lpVtbl -> Release(This) ) 


#define ConnectionEventsVt_InfoMessage(This,pError,adStatus,pConnection)	\
    ( (This)->lpVtbl -> InfoMessage(This,pError,adStatus,pConnection) ) 

#define ConnectionEventsVt_BeginTransComplete(This,TransactionLevel,pError,adStatus,pConnection)	\
    ( (This)->lpVtbl -> BeginTransComplete(This,TransactionLevel,pError,adStatus,pConnection) ) 

#define ConnectionEventsVt_CommitTransComplete(This,pError,adStatus,pConnection)	\
    ( (This)->lpVtbl -> CommitTransComplete(This,pError,adStatus,pConnection) ) 

#define ConnectionEventsVt_RollbackTransComplete(This,pError,adStatus,pConnection)	\
    ( (This)->lpVtbl -> RollbackTransComplete(This,pError,adStatus,pConnection) ) 

#define ConnectionEventsVt_WillExecute(This,Source,CursorType,LockType,Options,adStatus,pCommand,pRecordset,pConnection)	\
    ( (This)->lpVtbl -> WillExecute(This,Source,CursorType,LockType,Options,adStatus,pCommand,pRecordset,pConnection) ) 

#define ConnectionEventsVt_ExecuteComplete(This,RecordsAffected,pError,adStatus,pCommand,pRecordset,pConnection)	\
    ( (This)->lpVtbl -> ExecuteComplete(This,RecordsAffected,pError,adStatus,pCommand,pRecordset,pConnection) ) 

#define ConnectionEventsVt_WillConnect(This,ConnectionString,UserID,Password,Options,adStatus,pConnection)	\
    ( (This)->lpVtbl -> WillConnect(This,ConnectionString,UserID,Password,Options,adStatus,pConnection) ) 

#define ConnectionEventsVt_ConnectComplete(This,pError,adStatus,pConnection)	\
    ( (This)->lpVtbl -> ConnectComplete(This,pError,adStatus,pConnection) ) 

#define ConnectionEventsVt_Disconnect(This,adStatus,pConnection)	\
    ( (This)->lpVtbl -> Disconnect(This,adStatus,pConnection) ) 

#endif /* COBJMACROS */


#endif 	/* C style interface */




#endif 	/* __ConnectionEventsVt_INTERFACE_DEFINED__ */


#ifndef __RecordsetEventsVt_INTERFACE_DEFINED__
#define __RecordsetEventsVt_INTERFACE_DEFINED__

/* interface RecordsetEventsVt */
/* [object][hidden][uuid] */ 


EXTERN_C const IID IID_RecordsetEventsVt;

#if defined(__cplusplus) && !defined(CINTERFACE)
    
    MIDL_INTERFACE("00000403-0000-0010-8000-00AA006D2EA4")
    RecordsetEventsVt : public IUnknown
    {
    public:
        virtual HRESULT __stdcall WillChangeField( 
            /* [in] */ long cFields,
            /* [in] */ VARIANT Fields,
            /* [out][in] */ EventStatusEnum *adStatus,
            /* [in] */ _Recordset *pRecordset) = 0;
        
        virtual HRESULT __stdcall FieldChangeComplete( 
            /* [in] */ long cFields,
            /* [in] */ VARIANT Fields,
            /* [in] */ Error *pError,
            /* [out][in] */ EventStatusEnum *adStatus,
            /* [in] */ _Recordset *pRecordset) = 0;
        
        virtual HRESULT __stdcall WillChangeRecord( 
            /* [in] */ EventReasonEnum adReason,
            /* [in] */ long cRecords,
            /* [out][in] */ EventStatusEnum *adStatus,
            /* [in] */ _Recordset *pRecordset) = 0;
        
        virtual HRESULT __stdcall RecordChangeComplete( 
            /* [in] */ EventReasonEnum adReason,
            /* [in] */ long cRecords,
            /* [in] */ Error *pError,
            /* [out][in] */ EventStatusEnum *adStatus,
            /* [in] */ _Recordset *pRecordset) = 0;
        
        virtual HRESULT __stdcall WillChangeRecordset( 
            /* [in] */ EventReasonEnum adReason,
            /* [out][in] */ EventStatusEnum *adStatus,
            /* [in] */ _Recordset *pRecordset) = 0;
        
        virtual HRESULT __stdcall RecordsetChangeComplete( 
            /* [in] */ EventReasonEnum adReason,
            /* [in] */ Error *pError,
            /* [out][in] */ EventStatusEnum *adStatus,
            /* [in] */ _Recordset *pRecordset) = 0;
        
        virtual HRESULT __stdcall WillMove( 
            /* [in] */ EventReasonEnum adReason,
            /* [out][in] */ EventStatusEnum *adStatus,
            /* [in] */ _Recordset *pRecordset) = 0;
        
        virtual HRESULT __stdcall MoveComplete( 
            /* [in] */ EventReasonEnum adReason,
            /* [in] */ Error *pError,
            /* [out][in] */ EventStatusEnum *adStatus,
            /* [in] */ _Recordset *pRecordset) = 0;
        
        virtual HRESULT __stdcall EndOfRecordset( 
            /* [out][in] */ VARIANT_BOOL *fMoreData,
            /* [out][in] */ EventStatusEnum *adStatus,
            /* [in] */ _Recordset *pRecordset) = 0;
        
        virtual HRESULT __stdcall FetchProgress( 
            /* [in] */ long Progress,
            /* [in] */ long MaxProgress,
            /* [out][in] */ EventStatusEnum *adStatus,
            /* [in] */ _Recordset *pRecordset) = 0;
        
        virtual HRESULT __stdcall FetchComplete( 
            /* [in] */ Error *pError,
            /* [out][in] */ EventStatusEnum *adStatus,
            /* [in] */ _Recordset *pRecordset) = 0;
        
    };
    
#else 	/* C style interface */

    typedef struct RecordsetEventsVtVtbl
    {
        BEGIN_INTERFACE
        
        HRESULT ( STDMETHODCALLTYPE *QueryInterface )( 
            RecordsetEventsVt * This,
            /* [in] */ REFIID riid,
            /* [annotation][iid_is][out] */ 
            __RPC__deref_out  void **ppvObject);
        
        ULONG ( STDMETHODCALLTYPE *AddRef )( 
            RecordsetEventsVt * This);
        
        ULONG ( STDMETHODCALLTYPE *Release )( 
            RecordsetEventsVt * This);
        
        HRESULT ( __stdcall *WillChangeField )( 
            RecordsetEventsVt * This,
            /* [in] */ long cFields,
            /* [in] */ VARIANT Fields,
            /* [out][in] */ EventStatusEnum *adStatus,
            /* [in] */ _Recordset *pRecordset);
        
        HRESULT ( __stdcall *FieldChangeComplete )( 
            RecordsetEventsVt * This,
            /* [in] */ long cFields,
            /* [in] */ VARIANT Fields,
            /* [in] */ Error *pError,
            /* [out][in] */ EventStatusEnum *adStatus,
            /* [in] */ _Recordset *pRecordset);
        
        HRESULT ( __stdcall *WillChangeRecord )( 
            RecordsetEventsVt * This,
            /* [in] */ EventReasonEnum adReason,
            /* [in] */ long cRecords,
            /* [out][in] */ EventStatusEnum *adStatus,
            /* [in] */ _Recordset *pRecordset);
        
        HRESULT ( __stdcall *RecordChangeComplete )( 
            RecordsetEventsVt * This,
            /* [in] */ EventReasonEnum adReason,
            /* [in] */ long cRecords,
            /* [in] */ Error *pError,
            /* [out][in] */ EventStatusEnum *adStatus,
            /* [in] */ _Recordset *pRecordset);
        
        HRESULT ( __stdcall *WillChangeRecordset )( 
            RecordsetEventsVt * This,
            /* [in] */ EventReasonEnum adReason,
            /* [out][in] */ EventStatusEnum *adStatus,
            /* [in] */ _Recordset *pRecordset);
        
        HRESULT ( __stdcall *RecordsetChangeComplete )( 
            RecordsetEventsVt * This,
            /* [in] */ EventReasonEnum adReason,
            /* [in] */ Error *pError,
            /* [out][in] */ EventStatusEnum *adStatus,
            /* [in] */ _Recordset *pRecordset);
        
        HRESULT ( __stdcall *WillMove )( 
            RecordsetEventsVt * This,
            /* [in] */ EventReasonEnum adReason,
            /* [out][in] */ EventStatusEnum *adStatus,
            /* [in] */ _Recordset *pRecordset);
        
        HRESULT ( __stdcall *MoveComplete )( 
            RecordsetEventsVt * This,
            /* [in] */ EventReasonEnum adReason,
            /* [in] */ Error *pError,
            /* [out][in] */ EventStatusEnum *adStatus,
            /* [in] */ _Recordset *pRecordset);
        
        HRESULT ( __stdcall *EndOfRecordset )( 
            RecordsetEventsVt * This,
            /* [out][in] */ VARIANT_BOOL *fMoreData,
            /* [out][in] */ EventStatusEnum *adStatus,
            /* [in] */ _Recordset *pRecordset);
        
        HRESULT ( __stdcall *FetchProgress )( 
            RecordsetEventsVt * This,
            /* [in] */ long Progress,
            /* [in] */ long MaxProgress,
            /* [out][in] */ EventStatusEnum *adStatus,
            /* [in] */ _Recordset *pRecordset);
        
        HRESULT ( __stdcall *FetchComplete )( 
            RecordsetEventsVt * This,
            /* [in] */ Error *pError,
            /* [out][in] */ EventStatusEnum *adStatus,
            /* [in] */ _Recordset *pRecordset);
        
        END_INTERFACE
    } RecordsetEventsVtVtbl;

    interface RecordsetEventsVt
    {
        CONST_VTBL struct RecordsetEventsVtVtbl *lpVtbl;
    };

    

#ifdef COBJMACROS


#define RecordsetEventsVt_QueryInterface(This,riid,ppvObject)	\
    ( (This)->lpVtbl -> QueryInterface(This,riid,ppvObject) ) 

#define RecordsetEventsVt_AddRef(This)	\
    ( (This)->lpVtbl -> AddRef(This) ) 

#define RecordsetEventsVt_Release(This)	\
    ( (This)->lpVtbl -> Release(This) ) 


#define RecordsetEventsVt_WillChangeField(This,cFields,Fields,adStatus,pRecordset)	\
    ( (This)->lpVtbl -> WillChangeField(This,cFields,Fields,adStatus,pRecordset) ) 

#define RecordsetEventsVt_FieldChangeComplete(This,cFields,Fields,pError,adStatus,pRecordset)	\
    ( (This)->lpVtbl -> FieldChangeComplete(This,cFields,Fields,pError,adStatus,pRecordset) ) 

#define RecordsetEventsVt_WillChangeRecord(This,adReason,cRecords,adStatus,pRecordset)	\
    ( (This)->lpVtbl -> WillChangeRecord(This,adReason,cRecords,adStatus,pRecordset) ) 

#define RecordsetEventsVt_RecordChangeComplete(This,adReason,cRecords,pError,adStatus,pRecordset)	\
    ( (This)->lpVtbl -> RecordChangeComplete(This,adReason,cRecords,pError,adStatus,pRecordset) ) 

#define RecordsetEventsVt_WillChangeRecordset(This,adReason,adStatus,pRecordset)	\
    ( (This)->lpVtbl -> WillChangeRecordset(This,adReason,adStatus,pRecordset) ) 

#define RecordsetEventsVt_RecordsetChangeComplete(This,adReason,pError,adStatus,pRecordset)	\
    ( (This)->lpVtbl -> RecordsetChangeComplete(This,adReason,pError,adStatus,pRecordset) ) 

#define RecordsetEventsVt_WillMove(This,adReason,adStatus,pRecordset)	\
    ( (This)->lpVtbl -> WillMove(This,adReason,adStatus,pRecordset) ) 

#define RecordsetEventsVt_MoveComplete(This,adReason,pError,adStatus,pRecordset)	\
    ( (This)->lpVtbl -> MoveComplete(This,adReason,pError,adStatus,pRecordset) ) 

#define RecordsetEventsVt_EndOfRecordset(This,fMoreData,adStatus,pRecordset)	\
    ( (This)->lpVtbl -> EndOfRecordset(This,fMoreData,adStatus,pRecordset) ) 

#define RecordsetEventsVt_FetchProgress(This,Progress,MaxProgress,adStatus,pRecordset)	\
    ( (This)->lpVtbl -> FetchProgress(This,Progress,MaxProgress,adStatus,pRecordset) ) 

#define RecordsetEventsVt_FetchComplete(This,pError,adStatus,pRecordset)	\
    ( (This)->lpVtbl -> FetchComplete(This,pError,adStatus,pRecordset) ) 

#endif /* COBJMACROS */


#endif 	/* C style interface */




#endif 	/* __RecordsetEventsVt_INTERFACE_DEFINED__ */


#ifndef __ADOConnectionConstruction15_INTERFACE_DEFINED__
#define __ADOConnectionConstruction15_INTERFACE_DEFINED__

/* interface ADOConnectionConstruction15 */
/* [object][restricted][uuid] */ 


EXTERN_C const IID IID_ADOConnectionConstruction15;

#if defined(__cplusplus) && !defined(CINTERFACE)
    
    MIDL_INTERFACE("00000516-0000-0010-8000-00AA006D2EA4")
    ADOConnectionConstruction15 : public IUnknown
    {
    public:
        virtual /* [propget] */ HRESULT __stdcall get_DSO( 
            /* [retval][out] */ IUnknown **ppDSO) = 0;
        
        virtual /* [propget] */ HRESULT __stdcall get_Session( 
            /* [retval][out] */ IUnknown **ppSession) = 0;
        
        virtual HRESULT __stdcall WrapDSOandSession( 
            /* [in] */ IUnknown *pDSO,
            /* [in] */ IUnknown *pSession) = 0;
        
    };
    
#else 	/* C style interface */

    typedef struct ADOConnectionConstruction15Vtbl
    {
        BEGIN_INTERFACE
        
        HRESULT ( STDMETHODCALLTYPE *QueryInterface )( 
            ADOConnectionConstruction15 * This,
            /* [in] */ REFIID riid,
            /* [annotation][iid_is][out] */ 
            __RPC__deref_out  void **ppvObject);
        
        ULONG ( STDMETHODCALLTYPE *AddRef )( 
            ADOConnectionConstruction15 * This);
        
        ULONG ( STDMETHODCALLTYPE *Release )( 
            ADOConnectionConstruction15 * This);
        
        /* [propget] */ HRESULT ( __stdcall *get_DSO )( 
            ADOConnectionConstruction15 * This,
            /* [retval][out] */ IUnknown **ppDSO);
        
        /* [propget] */ HRESULT ( __stdcall *get_Session )( 
            ADOConnectionConstruction15 * This,
            /* [retval][out] */ IUnknown **ppSession);
        
        HRESULT ( __stdcall *WrapDSOandSession )( 
            ADOConnectionConstruction15 * This,
            /* [in] */ IUnknown *pDSO,
            /* [in] */ IUnknown *pSession);
        
        END_INTERFACE
    } ADOConnectionConstruction15Vtbl;

    interface ADOConnectionConstruction15
    {
        CONST_VTBL struct ADOConnectionConstruction15Vtbl *lpVtbl;
    };

    

#ifdef COBJMACROS


#define ADOConnectionConstruction15_QueryInterface(This,riid,ppvObject)	\
    ( (This)->lpVtbl -> QueryInterface(This,riid,ppvObject) ) 

#define ADOConnectionConstruction15_AddRef(This)	\
    ( (This)->lpVtbl -> AddRef(This) ) 

#define ADOConnectionConstruction15_Release(This)	\
    ( (This)->lpVtbl -> Release(This) ) 


#define ADOConnectionConstruction15_get_DSO(This,ppDSO)	\
    ( (This)->lpVtbl -> get_DSO(This,ppDSO) ) 

#define ADOConnectionConstruction15_get_Session(This,ppSession)	\
    ( (This)->lpVtbl -> get_Session(This,ppSession) ) 

#define ADOConnectionConstruction15_WrapDSOandSession(This,pDSO,pSession)	\
    ( (This)->lpVtbl -> WrapDSOandSession(This,pDSO,pSession) ) 

#endif /* COBJMACROS */


#endif 	/* C style interface */




#endif 	/* __ADOConnectionConstruction15_INTERFACE_DEFINED__ */


#ifndef __ADOConnectionConstruction_INTERFACE_DEFINED__
#define __ADOConnectionConstruction_INTERFACE_DEFINED__

/* interface ADOConnectionConstruction */
/* [object][restricted][uuid] */ 


EXTERN_C const IID IID_ADOConnectionConstruction;

#if defined(__cplusplus) && !defined(CINTERFACE)
    
    MIDL_INTERFACE("00000551-0000-0010-8000-00AA006D2EA4")
    ADOConnectionConstruction : public ADOConnectionConstruction15
    {
    public:
    };
    
#else 	/* C style interface */

    typedef struct ADOConnectionConstructionVtbl
    {
        BEGIN_INTERFACE
        
        HRESULT ( STDMETHODCALLTYPE *QueryInterface )( 
            ADOConnectionConstruction * This,
            /* [in] */ REFIID riid,
            /* [annotation][iid_is][out] */ 
            __RPC__deref_out  void **ppvObject);
        
        ULONG ( STDMETHODCALLTYPE *AddRef )( 
            ADOConnectionConstruction * This);
        
        ULONG ( STDMETHODCALLTYPE *Release )( 
            ADOConnectionConstruction * This);
        
        /* [propget] */ HRESULT ( __stdcall *get_DSO )( 
            ADOConnectionConstruction * This,
            /* [retval][out] */ IUnknown **ppDSO);
        
        /* [propget] */ HRESULT ( __stdcall *get_Session )( 
            ADOConnectionConstruction * This,
            /* [retval][out] */ IUnknown **ppSession);
        
        HRESULT ( __stdcall *WrapDSOandSession )( 
            ADOConnectionConstruction * This,
            /* [in] */ IUnknown *pDSO,
            /* [in] */ IUnknown *pSession);
        
        END_INTERFACE
    } ADOConnectionConstructionVtbl;

    interface ADOConnectionConstruction
    {
        CONST_VTBL struct ADOConnectionConstructionVtbl *lpVtbl;
    };

    

#ifdef COBJMACROS


#define ADOConnectionConstruction_QueryInterface(This,riid,ppvObject)	\
    ( (This)->lpVtbl -> QueryInterface(This,riid,ppvObject) ) 

#define ADOConnectionConstruction_AddRef(This)	\
    ( (This)->lpVtbl -> AddRef(This) ) 

#define ADOConnectionConstruction_Release(This)	\
    ( (This)->lpVtbl -> Release(This) ) 


#define ADOConnectionConstruction_get_DSO(This,ppDSO)	\
    ( (This)->lpVtbl -> get_DSO(This,ppDSO) ) 

#define ADOConnectionConstruction_get_Session(This,ppSession)	\
    ( (This)->lpVtbl -> get_Session(This,ppSession) ) 

#define ADOConnectionConstruction_WrapDSOandSession(This,pDSO,pSession)	\
    ( (This)->lpVtbl -> WrapDSOandSession(This,pDSO,pSession) ) 


#endif /* COBJMACROS */


#endif 	/* C style interface */




#endif 	/* __ADOConnectionConstruction_INTERFACE_DEFINED__ */


#ifndef __ADORecordsetConstruction_INTERFACE_DEFINED__
#define __ADORecordsetConstruction_INTERFACE_DEFINED__

/* interface ADORecordsetConstruction */
/* [object][restricted][uuid] */ 


EXTERN_C const IID IID_ADORecordsetConstruction;

#if defined(__cplusplus) && !defined(CINTERFACE)
    
    MIDL_INTERFACE("00000283-0000-0010-8000-00AA006D2EA4")
    ADORecordsetConstruction : public IDispatch
    {
    public:
        virtual /* [propget] */ HRESULT __stdcall get_Rowset( 
            /* [retval][out] */ IUnknown **ppRowset) = 0;
        
        virtual /* [propput] */ HRESULT __stdcall put_Rowset( 
            /* [in] */ IUnknown *ppRowset) = 0;
        
        virtual /* [propget] */ HRESULT __stdcall get_Chapter( 
            /* [retval][out] */ ADO_LONGPTR *plChapter) = 0;
        
        virtual /* [propput] */ HRESULT __stdcall put_Chapter( 
            /* [in] */ ADO_LONGPTR plChapter) = 0;
        
        virtual /* [propget] */ HRESULT __stdcall get_RowPosition( 
            /* [retval][out] */ IUnknown **ppRowPos) = 0;
        
        virtual /* [propput] */ HRESULT __stdcall put_RowPosition( 
            /* [in] */ IUnknown *ppRowPos) = 0;
        
    };
    
#else 	/* C style interface */

    typedef struct ADORecordsetConstructionVtbl
    {
        BEGIN_INTERFACE
        
        HRESULT ( STDMETHODCALLTYPE *QueryInterface )( 
            ADORecordsetConstruction * This,
            /* [in] */ REFIID riid,
            /* [annotation][iid_is][out] */ 
            __RPC__deref_out  void **ppvObject);
        
        ULONG ( STDMETHODCALLTYPE *AddRef )( 
            ADORecordsetConstruction * This);
        
        ULONG ( STDMETHODCALLTYPE *Release )( 
            ADORecordsetConstruction * This);
        
        HRESULT ( STDMETHODCALLTYPE *GetTypeInfoCount )( 
            ADORecordsetConstruction * This,
            /* [out] */ UINT *pctinfo);
        
        HRESULT ( STDMETHODCALLTYPE *GetTypeInfo )( 
            ADORecordsetConstruction * This,
            /* [in] */ UINT iTInfo,
            /* [in] */ LCID lcid,
            /* [out] */ ITypeInfo **ppTInfo);
        
        HRESULT ( STDMETHODCALLTYPE *GetIDsOfNames )( 
            ADORecordsetConstruction * This,
            /* [in] */ REFIID riid,
            /* [size_is][in] */ LPOLESTR *rgszNames,
            /* [range][in] */ UINT cNames,
            /* [in] */ LCID lcid,
            /* [size_is][out] */ DISPID *rgDispId);
        
        /* [local] */ HRESULT ( STDMETHODCALLTYPE *Invoke )( 
            ADORecordsetConstruction * This,
            /* [in] */ DISPID dispIdMember,
            /* [in] */ REFIID riid,
            /* [in] */ LCID lcid,
            /* [in] */ WORD wFlags,
            /* [out][in] */ DISPPARAMS *pDispParams,
            /* [out] */ VARIANT *pVarResult,
            /* [out] */ EXCEPINFO *pExcepInfo,
            /* [out] */ UINT *puArgErr);
        
        /* [propget] */ HRESULT ( __stdcall *get_Rowset )( 
            ADORecordsetConstruction * This,
            /* [retval][out] */ IUnknown **ppRowset);
        
        /* [propput] */ HRESULT ( __stdcall *put_Rowset )( 
            ADORecordsetConstruction * This,
            /* [in] */ IUnknown *ppRowset);
        
        /* [propget] */ HRESULT ( __stdcall *get_Chapter )( 
            ADORecordsetConstruction * This,
            /* [retval][out] */ ADO_LONGPTR *plChapter);
        
        /* [propput] */ HRESULT ( __stdcall *put_Chapter )( 
            ADORecordsetConstruction * This,
            /* [in] */ ADO_LONGPTR plChapter);
        
        /* [propget] */ HRESULT ( __stdcall *get_RowPosition )( 
            ADORecordsetConstruction * This,
            /* [retval][out] */ IUnknown **ppRowPos);
        
        /* [propput] */ HRESULT ( __stdcall *put_RowPosition )( 
            ADORecordsetConstruction * This,
            /* [in] */ IUnknown *ppRowPos);
        
        END_INTERFACE
    } ADORecordsetConstructionVtbl;

    interface ADORecordsetConstruction
    {
        CONST_VTBL struct ADORecordsetConstructionVtbl *lpVtbl;
    };

    

#ifdef COBJMACROS


#define ADORecordsetConstruction_QueryInterface(This,riid,ppvObject)	\
    ( (This)->lpVtbl -> QueryInterface(This,riid,ppvObject) ) 

#define ADORecordsetConstruction_AddRef(This)	\
    ( (This)->lpVtbl -> AddRef(This) ) 

#define ADORecordsetConstruction_Release(This)	\
    ( (This)->lpVtbl -> Release(This) ) 


#define ADORecordsetConstruction_GetTypeInfoCount(This,pctinfo)	\
    ( (This)->lpVtbl -> GetTypeInfoCount(This,pctinfo) ) 

#define ADORecordsetConstruction_GetTypeInfo(This,iTInfo,lcid,ppTInfo)	\
    ( (This)->lpVtbl -> GetTypeInfo(This,iTInfo,lcid,ppTInfo) ) 

#define ADORecordsetConstruction_GetIDsOfNames(This,riid,rgszNames,cNames,lcid,rgDispId)	\
    ( (This)->lpVtbl -> GetIDsOfNames(This,riid,rgszNames,cNames,lcid,rgDispId) ) 

#define ADORecordsetConstruction_Invoke(This,dispIdMember,riid,lcid,wFlags,pDispParams,pVarResult,pExcepInfo,puArgErr)	\
    ( (This)->lpVtbl -> Invoke(This,dispIdMember,riid,lcid,wFlags,pDispParams,pVarResult,pExcepInfo,puArgErr) ) 


#define ADORecordsetConstruction_get_Rowset(This,ppRowset)	\
    ( (This)->lpVtbl -> get_Rowset(This,ppRowset) ) 

#define ADORecordsetConstruction_put_Rowset(This,ppRowset)	\
    ( (This)->lpVtbl -> put_Rowset(This,ppRowset) ) 

#define ADORecordsetConstruction_get_Chapter(This,plChapter)	\
    ( (This)->lpVtbl -> get_Chapter(This,plChapter) ) 

#define ADORecordsetConstruction_put_Chapter(This,plChapter)	\
    ( (This)->lpVtbl -> put_Chapter(This,plChapter) ) 

#define ADORecordsetConstruction_get_RowPosition(This,ppRowPos)	\
    ( (This)->lpVtbl -> get_RowPosition(This,ppRowPos) ) 

#define ADORecordsetConstruction_put_RowPosition(This,ppRowPos)	\
    ( (This)->lpVtbl -> put_RowPosition(This,ppRowPos) ) 

#endif /* COBJMACROS */


#endif 	/* C style interface */




#endif 	/* __ADORecordsetConstruction_INTERFACE_DEFINED__ */


#ifndef __ADOCommandConstruction_INTERFACE_DEFINED__
#define __ADOCommandConstruction_INTERFACE_DEFINED__

/* interface ADOCommandConstruction */
/* [object][restricted][uuid] */ 


EXTERN_C const IID IID_ADOCommandConstruction;

#if defined(__cplusplus) && !defined(CINTERFACE)
    
    MIDL_INTERFACE("00000517-0000-0010-8000-00AA006D2EA4")
    ADOCommandConstruction : public IUnknown
    {
    public:
        virtual /* [propget] */ HRESULT __stdcall get_OLEDBCommand( 
            /* [retval][out] */ IUnknown **ppOLEDBCommand) = 0;
        
        virtual /* [propput] */ HRESULT __stdcall put_OLEDBCommand( 
            /* [in] */ IUnknown *ppOLEDBCommand) = 0;
        
    };
    
#else 	/* C style interface */

    typedef struct ADOCommandConstructionVtbl
    {
        BEGIN_INTERFACE
        
        HRESULT ( STDMETHODCALLTYPE *QueryInterface )( 
            ADOCommandConstruction * This,
            /* [in] */ REFIID riid,
            /* [annotation][iid_is][out] */ 
            __RPC__deref_out  void **ppvObject);
        
        ULONG ( STDMETHODCALLTYPE *AddRef )( 
            ADOCommandConstruction * This);
        
        ULONG ( STDMETHODCALLTYPE *Release )( 
            ADOCommandConstruction * This);
        
        /* [propget] */ HRESULT ( __stdcall *get_OLEDBCommand )( 
            ADOCommandConstruction * This,
            /* [retval][out] */ IUnknown **ppOLEDBCommand);
        
        /* [propput] */ HRESULT ( __stdcall *put_OLEDBCommand )( 
            ADOCommandConstruction * This,
            /* [in] */ IUnknown *ppOLEDBCommand);
        
        END_INTERFACE
    } ADOCommandConstructionVtbl;

    interface ADOCommandConstruction
    {
        CONST_VTBL struct ADOCommandConstructionVtbl *lpVtbl;
    };

    

#ifdef COBJMACROS


#define ADOCommandConstruction_QueryInterface(This,riid,ppvObject)	\
    ( (This)->lpVtbl -> QueryInterface(This,riid,ppvObject) ) 

#define ADOCommandConstruction_AddRef(This)	\
    ( (This)->lpVtbl -> AddRef(This) ) 

#define ADOCommandConstruction_Release(This)	\
    ( (This)->lpVtbl -> Release(This) ) 


#define ADOCommandConstruction_get_OLEDBCommand(This,ppOLEDBCommand)	\
    ( (This)->lpVtbl -> get_OLEDBCommand(This,ppOLEDBCommand) ) 

#define ADOCommandConstruction_put_OLEDBCommand(This,ppOLEDBCommand)	\
    ( (This)->lpVtbl -> put_OLEDBCommand(This,ppOLEDBCommand) ) 

#endif /* COBJMACROS */


#endif 	/* C style interface */




#endif 	/* __ADOCommandConstruction_INTERFACE_DEFINED__ */


#ifndef __ADORecordConstruction_INTERFACE_DEFINED__
#define __ADORecordConstruction_INTERFACE_DEFINED__

/* interface ADORecordConstruction */
/* [object][restricted][uuid] */ 


EXTERN_C const IID IID_ADORecordConstruction;

#if defined(__cplusplus) && !defined(CINTERFACE)
    
    MIDL_INTERFACE("00000567-0000-0010-8000-00AA006D2EA4")
    ADORecordConstruction : public IDispatch
    {
    public:
        virtual /* [propget] */ HRESULT __stdcall get_Row( 
            /* [retval][out] */ IUnknown **ppRow) = 0;
        
        virtual /* [propput] */ HRESULT __stdcall put_Row( 
            /* [in] */ IUnknown *pRow) = 0;
        
        virtual /* [propput] */ HRESULT __stdcall put_ParentRow( 
            /* [in] */ IUnknown *pRow) = 0;
        
    };
    
#else 	/* C style interface */

    typedef struct ADORecordConstructionVtbl
    {
        BEGIN_INTERFACE
        
        HRESULT ( STDMETHODCALLTYPE *QueryInterface )( 
            ADORecordConstruction * This,
            /* [in] */ REFIID riid,
            /* [annotation][iid_is][out] */ 
            __RPC__deref_out  void **ppvObject);
        
        ULONG ( STDMETHODCALLTYPE *AddRef )( 
            ADORecordConstruction * This);
        
        ULONG ( STDMETHODCALLTYPE *Release )( 
            ADORecordConstruction * This);
        
        HRESULT ( STDMETHODCALLTYPE *GetTypeInfoCount )( 
            ADORecordConstruction * This,
            /* [out] */ UINT *pctinfo);
        
        HRESULT ( STDMETHODCALLTYPE *GetTypeInfo )( 
            ADORecordConstruction * This,
            /* [in] */ UINT iTInfo,
            /* [in] */ LCID lcid,
            /* [out] */ ITypeInfo **ppTInfo);
        
        HRESULT ( STDMETHODCALLTYPE *GetIDsOfNames )( 
            ADORecordConstruction * This,
            /* [in] */ REFIID riid,
            /* [size_is][in] */ LPOLESTR *rgszNames,
            /* [range][in] */ UINT cNames,
            /* [in] */ LCID lcid,
            /* [size_is][out] */ DISPID *rgDispId);
        
        /* [local] */ HRESULT ( STDMETHODCALLTYPE *Invoke )( 
            ADORecordConstruction * This,
            /* [in] */ DISPID dispIdMember,
            /* [in] */ REFIID riid,
            /* [in] */ LCID lcid,
            /* [in] */ WORD wFlags,
            /* [out][in] */ DISPPARAMS *pDispParams,
            /* [out] */ VARIANT *pVarResult,
            /* [out] */ EXCEPINFO *pExcepInfo,
            /* [out] */ UINT *puArgErr);
        
        /* [propget] */ HRESULT ( __stdcall *get_Row )( 
            ADORecordConstruction * This,
            /* [retval][out] */ IUnknown **ppRow);
        
        /* [propput] */ HRESULT ( __stdcall *put_Row )( 
            ADORecordConstruction * This,
            /* [in] */ IUnknown *pRow);
        
        /* [propput] */ HRESULT ( __stdcall *put_ParentRow )( 
            ADORecordConstruction * This,
            /* [in] */ IUnknown *pRow);
        
        END_INTERFACE
    } ADORecordConstructionVtbl;

    interface ADORecordConstruction
    {
        CONST_VTBL struct ADORecordConstructionVtbl *lpVtbl;
    };

    

#ifdef COBJMACROS


#define ADORecordConstruction_QueryInterface(This,riid,ppvObject)	\
    ( (This)->lpVtbl -> QueryInterface(This,riid,ppvObject) ) 

#define ADORecordConstruction_AddRef(This)	\
    ( (This)->lpVtbl -> AddRef(This) ) 

#define ADORecordConstruction_Release(This)	\
    ( (This)->lpVtbl -> Release(This) ) 


#define ADORecordConstruction_GetTypeInfoCount(This,pctinfo)	\
    ( (This)->lpVtbl -> GetTypeInfoCount(This,pctinfo) ) 

#define ADORecordConstruction_GetTypeInfo(This,iTInfo,lcid,ppTInfo)	\
    ( (This)->lpVtbl -> GetTypeInfo(This,iTInfo,lcid,ppTInfo) ) 

#define ADORecordConstruction_GetIDsOfNames(This,riid,rgszNames,cNames,lcid,rgDispId)	\
    ( (This)->lpVtbl -> GetIDsOfNames(This,riid,rgszNames,cNames,lcid,rgDispId) ) 

#define ADORecordConstruction_Invoke(This,dispIdMember,riid,lcid,wFlags,pDispParams,pVarResult,pExcepInfo,puArgErr)	\
    ( (This)->lpVtbl -> Invoke(This,dispIdMember,riid,lcid,wFlags,pDispParams,pVarResult,pExcepInfo,puArgErr) ) 


#define ADORecordConstruction_get_Row(This,ppRow)	\
    ( (This)->lpVtbl -> get_Row(This,ppRow) ) 

#define ADORecordConstruction_put_Row(This,pRow)	\
    ( (This)->lpVtbl -> put_Row(This,pRow) ) 

#define ADORecordConstruction_put_ParentRow(This,pRow)	\
    ( (This)->lpVtbl -> put_ParentRow(This,pRow) ) 

#endif /* COBJMACROS */


#endif 	/* C style interface */




#endif 	/* __ADORecordConstruction_INTERFACE_DEFINED__ */


#ifndef __ADOStreamConstruction_INTERFACE_DEFINED__
#define __ADOStreamConstruction_INTERFACE_DEFINED__

/* interface ADOStreamConstruction */
/* [object][restricted][uuid] */ 


EXTERN_C const IID IID_ADOStreamConstruction;

#if defined(__cplusplus) && !defined(CINTERFACE)
    
    MIDL_INTERFACE("00000568-0000-0010-8000-00AA006D2EA4")
    ADOStreamConstruction : public IDispatch
    {
    public:
        virtual /* [propget] */ HRESULT __stdcall get_Stream( 
            /* [retval][out] */ IUnknown **ppStm) = 0;
        
        virtual /* [propput] */ HRESULT __stdcall put_Stream( 
            /* [in] */ IUnknown *pStm) = 0;
        
    };
    
#else 	/* C style interface */

    typedef struct ADOStreamConstructionVtbl
    {
        BEGIN_INTERFACE
        
        HRESULT ( STDMETHODCALLTYPE *QueryInterface )( 
            ADOStreamConstruction * This,
            /* [in] */ REFIID riid,
            /* [annotation][iid_is][out] */ 
            __RPC__deref_out  void **ppvObject);
        
        ULONG ( STDMETHODCALLTYPE *AddRef )( 
            ADOStreamConstruction * This);
        
        ULONG ( STDMETHODCALLTYPE *Release )( 
            ADOStreamConstruction * This);
        
        HRESULT ( STDMETHODCALLTYPE *GetTypeInfoCount )( 
            ADOStreamConstruction * This,
            /* [out] */ UINT *pctinfo);
        
        HRESULT ( STDMETHODCALLTYPE *GetTypeInfo )( 
            ADOStreamConstruction * This,
            /* [in] */ UINT iTInfo,
            /* [in] */ LCID lcid,
            /* [out] */ ITypeInfo **ppTInfo);
        
        HRESULT ( STDMETHODCALLTYPE *GetIDsOfNames )( 
            ADOStreamConstruction * This,
            /* [in] */ REFIID riid,
            /* [size_is][in] */ LPOLESTR *rgszNames,
            /* [range][in] */ UINT cNames,
            /* [in] */ LCID lcid,
            /* [size_is][out] */ DISPID *rgDispId);
        
        /* [local] */ HRESULT ( STDMETHODCALLTYPE *Invoke )( 
            ADOStreamConstruction * This,
            /* [in] */ DISPID dispIdMember,
            /* [in] */ REFIID riid,
            /* [in] */ LCID lcid,
            /* [in] */ WORD wFlags,
            /* [out][in] */ DISPPARAMS *pDispParams,
            /* [out] */ VARIANT *pVarResult,
            /* [out] */ EXCEPINFO *pExcepInfo,
            /* [out] */ UINT *puArgErr);
        
        /* [propget] */ HRESULT ( __stdcall *get_Stream )( 
            ADOStreamConstruction * This,
            /* [retval][out] */ IUnknown **ppStm);
        
        /* [propput] */ HRESULT ( __stdcall *put_Stream )( 
            ADOStreamConstruction * This,
            /* [in] */ IUnknown *pStm);
        
        END_INTERFACE
    } ADOStreamConstructionVtbl;

    interface ADOStreamConstruction
    {
        CONST_VTBL struct ADOStreamConstructionVtbl *lpVtbl;
    };

    

#ifdef COBJMACROS


#define ADOStreamConstruction_QueryInterface(This,riid,ppvObject)	\
    ( (This)->lpVtbl -> QueryInterface(This,riid,ppvObject) ) 

#define ADOStreamConstruction_AddRef(This)	\
    ( (This)->lpVtbl -> AddRef(This) ) 

#define ADOStreamConstruction_Release(This)	\
    ( (This)->lpVtbl -> Release(This) ) 


#define ADOStreamConstruction_GetTypeInfoCount(This,pctinfo)	\
    ( (This)->lpVtbl -> GetTypeInfoCount(This,pctinfo) ) 

#define ADOStreamConstruction_GetTypeInfo(This,iTInfo,lcid,ppTInfo)	\
    ( (This)->lpVtbl -> GetTypeInfo(This,iTInfo,lcid,ppTInfo) ) 

#define ADOStreamConstruction_GetIDsOfNames(This,riid,rgszNames,cNames,lcid,rgDispId)	\
    ( (This)->lpVtbl -> GetIDsOfNames(This,riid,rgszNames,cNames,lcid,rgDispId) ) 

#define ADOStreamConstruction_Invoke(This,dispIdMember,riid,lcid,wFlags,pDispParams,pVarResult,pExcepInfo,puArgErr)	\
    ( (This)->lpVtbl -> Invoke(This,dispIdMember,riid,lcid,wFlags,pDispParams,pVarResult,pExcepInfo,puArgErr) ) 


#define ADOStreamConstruction_get_Stream(This,ppStm)	\
    ( (This)->lpVtbl -> get_Stream(This,ppStm) ) 

#define ADOStreamConstruction_put_Stream(This,pStm)	\
    ( (This)->lpVtbl -> put_Stream(This,pStm) ) 

#endif /* COBJMACROS */


#endif 	/* C style interface */




#endif 	/* __ADOStreamConstruction_INTERFACE_DEFINED__ */



#ifndef __ADODB_LIBRARY_DEFINED__
#define __ADODB_LIBRARY_DEFINED__

/* library ADODB */
/* [helpstring][version][uuid] */ 


EXTERN_C const IID LIBID_ADODB;

EXTERN_C const CLSID CLSID_Connection;

#ifdef __cplusplus

class DECLSPEC_UUID("00000514-0000-0010-8000-00AA006D2EA4")
Connection;
#endif

EXTERN_C const CLSID CLSID_Command;

#ifdef __cplusplus

class DECLSPEC_UUID("00000507-0000-0010-8000-00AA006D2EA4")
Command;
#endif

EXTERN_C const CLSID CLSID_Recordset;

#ifdef __cplusplus

class DECLSPEC_UUID("00000535-0000-0010-8000-00AA006D2EA4")
Recordset;
#endif

EXTERN_C const CLSID CLSID_Parameter;

#ifdef __cplusplus

class DECLSPEC_UUID("0000050B-0000-0010-8000-00AA006D2EA4")
Parameter;
#endif

EXTERN_C const CLSID CLSID_Record;

#ifdef __cplusplus

class DECLSPEC_UUID("00000560-0000-0010-8000-00AA006D2EA4")
Record;
#endif

EXTERN_C const CLSID CLSID_Stream;

#ifdef __cplusplus

class DECLSPEC_UUID("00000566-0000-0010-8000-00AA006D2EA4")
Stream;
#endif
#endif /* __ADODB_LIBRARY_DEFINED__ */

/* Additional Prototypes for ALL interfaces */

unsigned long             __RPC_USER  BSTR_UserSize(     unsigned long *, unsigned long            , BSTR * ); 
unsigned char * __RPC_USER  BSTR_UserMarshal(  unsigned long *, unsigned char *, BSTR * ); 
unsigned char * __RPC_USER  BSTR_UserUnmarshal(unsigned long *, unsigned char *, BSTR * ); 
void                      __RPC_USER  BSTR_UserFree(     unsigned long *, BSTR * ); 

unsigned long             __RPC_USER  VARIANT_UserSize(     unsigned long *, unsigned long            , VARIANT * ); 
unsigned char * __RPC_USER  VARIANT_UserMarshal(  unsigned long *, unsigned char *, VARIANT * ); 
unsigned char * __RPC_USER  VARIANT_UserUnmarshal(unsigned long *, unsigned char *, VARIANT * ); 
void                      __RPC_USER  VARIANT_UserFree(     unsigned long *, VARIANT * ); 

/* end of Additional Prototypes */

#ifdef __cplusplus
}
#endif

#endif


