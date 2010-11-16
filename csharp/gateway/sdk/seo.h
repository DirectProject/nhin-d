

/* this ALWAYS GENERATED file contains the definitions for the interfaces */


 /* File created by MIDL compiler version 7.00.0555 */
/* at Tue Aug 10 21:55:53 2010
 */
/* Compiler settings for C:\Program Files\Microsoft SDKs\Windows\v6.0A\Include\seo.idl:
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

#ifndef __seo_h__
#define __seo_h__

#if defined(_MSC_VER) && (_MSC_VER >= 1020)
#pragma once
#endif

/* Forward Declarations */ 

#ifndef __ISEODictionaryItem_FWD_DEFINED__
#define __ISEODictionaryItem_FWD_DEFINED__
typedef interface ISEODictionaryItem ISEODictionaryItem;
#endif 	/* __ISEODictionaryItem_FWD_DEFINED__ */


#ifndef __ISEODictionary_FWD_DEFINED__
#define __ISEODictionary_FWD_DEFINED__
typedef interface ISEODictionary ISEODictionary;
#endif 	/* __ISEODictionary_FWD_DEFINED__ */


#ifndef __IEventLock_FWD_DEFINED__
#define __IEventLock_FWD_DEFINED__
typedef interface IEventLock IEventLock;
#endif 	/* __IEventLock_FWD_DEFINED__ */


#ifndef __ISEORouter_FWD_DEFINED__
#define __ISEORouter_FWD_DEFINED__
typedef interface ISEORouter ISEORouter;
#endif 	/* __ISEORouter_FWD_DEFINED__ */


#ifndef __IMCISMessageFilter_FWD_DEFINED__
#define __IMCISMessageFilter_FWD_DEFINED__
typedef interface IMCISMessageFilter IMCISMessageFilter;
#endif 	/* __IMCISMessageFilter_FWD_DEFINED__ */


#ifndef __ISEOBindingRuleEngine_FWD_DEFINED__
#define __ISEOBindingRuleEngine_FWD_DEFINED__
typedef interface ISEOBindingRuleEngine ISEOBindingRuleEngine;
#endif 	/* __ISEOBindingRuleEngine_FWD_DEFINED__ */


#ifndef __ISEOEventSink_FWD_DEFINED__
#define __ISEOEventSink_FWD_DEFINED__
typedef interface ISEOEventSink ISEOEventSink;
#endif 	/* __ISEOEventSink_FWD_DEFINED__ */


#ifndef __ISEORegDictionary_FWD_DEFINED__
#define __ISEORegDictionary_FWD_DEFINED__
typedef interface ISEORegDictionary ISEORegDictionary;
#endif 	/* __ISEORegDictionary_FWD_DEFINED__ */


#ifndef __ISEOBindingConverter_FWD_DEFINED__
#define __ISEOBindingConverter_FWD_DEFINED__
typedef interface ISEOBindingConverter ISEOBindingConverter;
#endif 	/* __ISEOBindingConverter_FWD_DEFINED__ */


#ifndef __ISEODispatcher_FWD_DEFINED__
#define __ISEODispatcher_FWD_DEFINED__
typedef interface ISEODispatcher ISEODispatcher;
#endif 	/* __ISEODispatcher_FWD_DEFINED__ */


#ifndef __IEventDeliveryOptions_FWD_DEFINED__
#define __IEventDeliveryOptions_FWD_DEFINED__
typedef interface IEventDeliveryOptions IEventDeliveryOptions;
#endif 	/* __IEventDeliveryOptions_FWD_DEFINED__ */


#ifndef __IEventTypeSinks_FWD_DEFINED__
#define __IEventTypeSinks_FWD_DEFINED__
typedef interface IEventTypeSinks IEventTypeSinks;
#endif 	/* __IEventTypeSinks_FWD_DEFINED__ */


#ifndef __IEventType_FWD_DEFINED__
#define __IEventType_FWD_DEFINED__
typedef interface IEventType IEventType;
#endif 	/* __IEventType_FWD_DEFINED__ */


#ifndef __IEventPropertyBag_FWD_DEFINED__
#define __IEventPropertyBag_FWD_DEFINED__
typedef interface IEventPropertyBag IEventPropertyBag;
#endif 	/* __IEventPropertyBag_FWD_DEFINED__ */


#ifndef __IEventBinding_FWD_DEFINED__
#define __IEventBinding_FWD_DEFINED__
typedef interface IEventBinding IEventBinding;
#endif 	/* __IEventBinding_FWD_DEFINED__ */


#ifndef __IEventBindings_FWD_DEFINED__
#define __IEventBindings_FWD_DEFINED__
typedef interface IEventBindings IEventBindings;
#endif 	/* __IEventBindings_FWD_DEFINED__ */


#ifndef __IEventTypes_FWD_DEFINED__
#define __IEventTypes_FWD_DEFINED__
typedef interface IEventTypes IEventTypes;
#endif 	/* __IEventTypes_FWD_DEFINED__ */


#ifndef __IEventBindingManager_FWD_DEFINED__
#define __IEventBindingManager_FWD_DEFINED__
typedef interface IEventBindingManager IEventBindingManager;
#endif 	/* __IEventBindingManager_FWD_DEFINED__ */


#ifndef __IEventBindingManagerCopier_FWD_DEFINED__
#define __IEventBindingManagerCopier_FWD_DEFINED__
typedef interface IEventBindingManagerCopier IEventBindingManagerCopier;
#endif 	/* __IEventBindingManagerCopier_FWD_DEFINED__ */


#ifndef __IEventRouter_FWD_DEFINED__
#define __IEventRouter_FWD_DEFINED__
typedef interface IEventRouter IEventRouter;
#endif 	/* __IEventRouter_FWD_DEFINED__ */


#ifndef __IEventDispatcher_FWD_DEFINED__
#define __IEventDispatcher_FWD_DEFINED__
typedef interface IEventDispatcher IEventDispatcher;
#endif 	/* __IEventDispatcher_FWD_DEFINED__ */


#ifndef __IEventSource_FWD_DEFINED__
#define __IEventSource_FWD_DEFINED__
typedef interface IEventSource IEventSource;
#endif 	/* __IEventSource_FWD_DEFINED__ */


#ifndef __IEventSources_FWD_DEFINED__
#define __IEventSources_FWD_DEFINED__
typedef interface IEventSources IEventSources;
#endif 	/* __IEventSources_FWD_DEFINED__ */


#ifndef __IEventSourceType_FWD_DEFINED__
#define __IEventSourceType_FWD_DEFINED__
typedef interface IEventSourceType IEventSourceType;
#endif 	/* __IEventSourceType_FWD_DEFINED__ */


#ifndef __IEventSourceTypes_FWD_DEFINED__
#define __IEventSourceTypes_FWD_DEFINED__
typedef interface IEventSourceTypes IEventSourceTypes;
#endif 	/* __IEventSourceTypes_FWD_DEFINED__ */


#ifndef __IEventManager_FWD_DEFINED__
#define __IEventManager_FWD_DEFINED__
typedef interface IEventManager IEventManager;
#endif 	/* __IEventManager_FWD_DEFINED__ */


#ifndef __IEventDatabasePlugin_FWD_DEFINED__
#define __IEventDatabasePlugin_FWD_DEFINED__
typedef interface IEventDatabasePlugin IEventDatabasePlugin;
#endif 	/* __IEventDatabasePlugin_FWD_DEFINED__ */


#ifndef __IEventDatabaseManager_FWD_DEFINED__
#define __IEventDatabaseManager_FWD_DEFINED__
typedef interface IEventDatabaseManager IEventDatabaseManager;
#endif 	/* __IEventDatabaseManager_FWD_DEFINED__ */


#ifndef __IEventUtil_FWD_DEFINED__
#define __IEventUtil_FWD_DEFINED__
typedef interface IEventUtil IEventUtil;
#endif 	/* __IEventUtil_FWD_DEFINED__ */


#ifndef __IEventComCat_FWD_DEFINED__
#define __IEventComCat_FWD_DEFINED__
typedef interface IEventComCat IEventComCat;
#endif 	/* __IEventComCat_FWD_DEFINED__ */


#ifndef __IEventNotifyBindingChange_FWD_DEFINED__
#define __IEventNotifyBindingChange_FWD_DEFINED__
typedef interface IEventNotifyBindingChange IEventNotifyBindingChange;
#endif 	/* __IEventNotifyBindingChange_FWD_DEFINED__ */


#ifndef __IEventNotifyBindingChangeDisp_FWD_DEFINED__
#define __IEventNotifyBindingChangeDisp_FWD_DEFINED__
typedef interface IEventNotifyBindingChangeDisp IEventNotifyBindingChangeDisp;
#endif 	/* __IEventNotifyBindingChangeDisp_FWD_DEFINED__ */


#ifndef __ISEOInitObject_FWD_DEFINED__
#define __ISEOInitObject_FWD_DEFINED__
typedef interface ISEOInitObject ISEOInitObject;
#endif 	/* __ISEOInitObject_FWD_DEFINED__ */


#ifndef __IEventRuleEngine_FWD_DEFINED__
#define __IEventRuleEngine_FWD_DEFINED__
typedef interface IEventRuleEngine IEventRuleEngine;
#endif 	/* __IEventRuleEngine_FWD_DEFINED__ */


#ifndef __IEventPersistBinding_FWD_DEFINED__
#define __IEventPersistBinding_FWD_DEFINED__
typedef interface IEventPersistBinding IEventPersistBinding;
#endif 	/* __IEventPersistBinding_FWD_DEFINED__ */


#ifndef __IEventSinkNotify_FWD_DEFINED__
#define __IEventSinkNotify_FWD_DEFINED__
typedef interface IEventSinkNotify IEventSinkNotify;
#endif 	/* __IEventSinkNotify_FWD_DEFINED__ */


#ifndef __IEventSinkNotifyDisp_FWD_DEFINED__
#define __IEventSinkNotifyDisp_FWD_DEFINED__
typedef interface IEventSinkNotifyDisp IEventSinkNotifyDisp;
#endif 	/* __IEventSinkNotifyDisp_FWD_DEFINED__ */


#ifndef __IEventIsCacheable_FWD_DEFINED__
#define __IEventIsCacheable_FWD_DEFINED__
typedef interface IEventIsCacheable IEventIsCacheable;
#endif 	/* __IEventIsCacheable_FWD_DEFINED__ */


#ifndef __IEventCreateOptions_FWD_DEFINED__
#define __IEventCreateOptions_FWD_DEFINED__
typedef interface IEventCreateOptions IEventCreateOptions;
#endif 	/* __IEventCreateOptions_FWD_DEFINED__ */


#ifndef __IEventDispatcherChain_FWD_DEFINED__
#define __IEventDispatcherChain_FWD_DEFINED__
typedef interface IEventDispatcherChain IEventDispatcherChain;
#endif 	/* __IEventDispatcherChain_FWD_DEFINED__ */


#ifndef __ISEODictionaryItem_FWD_DEFINED__
#define __ISEODictionaryItem_FWD_DEFINED__
typedef interface ISEODictionaryItem ISEODictionaryItem;
#endif 	/* __ISEODictionaryItem_FWD_DEFINED__ */


#ifndef __ISEODictionary_FWD_DEFINED__
#define __ISEODictionary_FWD_DEFINED__
typedef interface ISEODictionary ISEODictionary;
#endif 	/* __ISEODictionary_FWD_DEFINED__ */


#ifndef __IEventLock_FWD_DEFINED__
#define __IEventLock_FWD_DEFINED__
typedef interface IEventLock IEventLock;
#endif 	/* __IEventLock_FWD_DEFINED__ */


#ifndef __ISEORouter_FWD_DEFINED__
#define __ISEORouter_FWD_DEFINED__
typedef interface ISEORouter ISEORouter;
#endif 	/* __ISEORouter_FWD_DEFINED__ */


#ifndef __IMCISMessageFilter_FWD_DEFINED__
#define __IMCISMessageFilter_FWD_DEFINED__
typedef interface IMCISMessageFilter IMCISMessageFilter;
#endif 	/* __IMCISMessageFilter_FWD_DEFINED__ */


#ifndef __ISEOBindingRuleEngine_FWD_DEFINED__
#define __ISEOBindingRuleEngine_FWD_DEFINED__
typedef interface ISEOBindingRuleEngine ISEOBindingRuleEngine;
#endif 	/* __ISEOBindingRuleEngine_FWD_DEFINED__ */


#ifndef __ISEOEventSink_FWD_DEFINED__
#define __ISEOEventSink_FWD_DEFINED__
typedef interface ISEOEventSink ISEOEventSink;
#endif 	/* __ISEOEventSink_FWD_DEFINED__ */


#ifndef __ISEORegDictionary_FWD_DEFINED__
#define __ISEORegDictionary_FWD_DEFINED__
typedef interface ISEORegDictionary ISEORegDictionary;
#endif 	/* __ISEORegDictionary_FWD_DEFINED__ */


#ifndef __ISEOBindingConverter_FWD_DEFINED__
#define __ISEOBindingConverter_FWD_DEFINED__
typedef interface ISEOBindingConverter ISEOBindingConverter;
#endif 	/* __ISEOBindingConverter_FWD_DEFINED__ */


#ifndef __ISEODispatcher_FWD_DEFINED__
#define __ISEODispatcher_FWD_DEFINED__
typedef interface ISEODispatcher ISEODispatcher;
#endif 	/* __ISEODispatcher_FWD_DEFINED__ */


#ifndef __IEventDeliveryOptions_FWD_DEFINED__
#define __IEventDeliveryOptions_FWD_DEFINED__
typedef interface IEventDeliveryOptions IEventDeliveryOptions;
#endif 	/* __IEventDeliveryOptions_FWD_DEFINED__ */


#ifndef __IEventTypeSinks_FWD_DEFINED__
#define __IEventTypeSinks_FWD_DEFINED__
typedef interface IEventTypeSinks IEventTypeSinks;
#endif 	/* __IEventTypeSinks_FWD_DEFINED__ */


#ifndef __IEventType_FWD_DEFINED__
#define __IEventType_FWD_DEFINED__
typedef interface IEventType IEventType;
#endif 	/* __IEventType_FWD_DEFINED__ */


#ifndef __IEventPropertyBag_FWD_DEFINED__
#define __IEventPropertyBag_FWD_DEFINED__
typedef interface IEventPropertyBag IEventPropertyBag;
#endif 	/* __IEventPropertyBag_FWD_DEFINED__ */


#ifndef __IEventBinding_FWD_DEFINED__
#define __IEventBinding_FWD_DEFINED__
typedef interface IEventBinding IEventBinding;
#endif 	/* __IEventBinding_FWD_DEFINED__ */


#ifndef __IEventBindings_FWD_DEFINED__
#define __IEventBindings_FWD_DEFINED__
typedef interface IEventBindings IEventBindings;
#endif 	/* __IEventBindings_FWD_DEFINED__ */


#ifndef __IEventTypes_FWD_DEFINED__
#define __IEventTypes_FWD_DEFINED__
typedef interface IEventTypes IEventTypes;
#endif 	/* __IEventTypes_FWD_DEFINED__ */


#ifndef __IEventBindingManager_FWD_DEFINED__
#define __IEventBindingManager_FWD_DEFINED__
typedef interface IEventBindingManager IEventBindingManager;
#endif 	/* __IEventBindingManager_FWD_DEFINED__ */


#ifndef __IEventSource_FWD_DEFINED__
#define __IEventSource_FWD_DEFINED__
typedef interface IEventSource IEventSource;
#endif 	/* __IEventSource_FWD_DEFINED__ */


#ifndef __IEventSources_FWD_DEFINED__
#define __IEventSources_FWD_DEFINED__
typedef interface IEventSources IEventSources;
#endif 	/* __IEventSources_FWD_DEFINED__ */


#ifndef __IEventSourceType_FWD_DEFINED__
#define __IEventSourceType_FWD_DEFINED__
typedef interface IEventSourceType IEventSourceType;
#endif 	/* __IEventSourceType_FWD_DEFINED__ */


#ifndef __IEventSourceTypes_FWD_DEFINED__
#define __IEventSourceTypes_FWD_DEFINED__
typedef interface IEventSourceTypes IEventSourceTypes;
#endif 	/* __IEventSourceTypes_FWD_DEFINED__ */


#ifndef __IEventManager_FWD_DEFINED__
#define __IEventManager_FWD_DEFINED__
typedef interface IEventManager IEventManager;
#endif 	/* __IEventManager_FWD_DEFINED__ */


#ifndef __ISEOInitObject_FWD_DEFINED__
#define __ISEOInitObject_FWD_DEFINED__
typedef interface ISEOInitObject ISEOInitObject;
#endif 	/* __ISEOInitObject_FWD_DEFINED__ */


#ifndef __IEventDatabasePlugin_FWD_DEFINED__
#define __IEventDatabasePlugin_FWD_DEFINED__
typedef interface IEventDatabasePlugin IEventDatabasePlugin;
#endif 	/* __IEventDatabasePlugin_FWD_DEFINED__ */


#ifndef __IEventDatabaseManager_FWD_DEFINED__
#define __IEventDatabaseManager_FWD_DEFINED__
typedef interface IEventDatabaseManager IEventDatabaseManager;
#endif 	/* __IEventDatabaseManager_FWD_DEFINED__ */


#ifndef __IEventUtil_FWD_DEFINED__
#define __IEventUtil_FWD_DEFINED__
typedef interface IEventUtil IEventUtil;
#endif 	/* __IEventUtil_FWD_DEFINED__ */


#ifndef __IEventComCat_FWD_DEFINED__
#define __IEventComCat_FWD_DEFINED__
typedef interface IEventComCat IEventComCat;
#endif 	/* __IEventComCat_FWD_DEFINED__ */


#ifndef __IEventNotifyBindingChange_FWD_DEFINED__
#define __IEventNotifyBindingChange_FWD_DEFINED__
typedef interface IEventNotifyBindingChange IEventNotifyBindingChange;
#endif 	/* __IEventNotifyBindingChange_FWD_DEFINED__ */


#ifndef __IEventNotifyBindingChangeDisp_FWD_DEFINED__
#define __IEventNotifyBindingChangeDisp_FWD_DEFINED__
typedef interface IEventNotifyBindingChangeDisp IEventNotifyBindingChangeDisp;
#endif 	/* __IEventNotifyBindingChangeDisp_FWD_DEFINED__ */


#ifndef __IEventRouter_FWD_DEFINED__
#define __IEventRouter_FWD_DEFINED__
typedef interface IEventRouter IEventRouter;
#endif 	/* __IEventRouter_FWD_DEFINED__ */


#ifndef __IEventDispatcher_FWD_DEFINED__
#define __IEventDispatcher_FWD_DEFINED__
typedef interface IEventDispatcher IEventDispatcher;
#endif 	/* __IEventDispatcher_FWD_DEFINED__ */


#ifndef __IEventRuleEngine_FWD_DEFINED__
#define __IEventRuleEngine_FWD_DEFINED__
typedef interface IEventRuleEngine IEventRuleEngine;
#endif 	/* __IEventRuleEngine_FWD_DEFINED__ */


#ifndef __IEventSinkNotify_FWD_DEFINED__
#define __IEventSinkNotify_FWD_DEFINED__
typedef interface IEventSinkNotify IEventSinkNotify;
#endif 	/* __IEventSinkNotify_FWD_DEFINED__ */


#ifndef __IEventSinkNotifyDisp_FWD_DEFINED__
#define __IEventSinkNotifyDisp_FWD_DEFINED__
typedef interface IEventSinkNotifyDisp IEventSinkNotifyDisp;
#endif 	/* __IEventSinkNotifyDisp_FWD_DEFINED__ */


#ifndef __IEventPersistBinding_FWD_DEFINED__
#define __IEventPersistBinding_FWD_DEFINED__
typedef interface IEventPersistBinding IEventPersistBinding;
#endif 	/* __IEventPersistBinding_FWD_DEFINED__ */


#ifndef __IEventIsCacheable_FWD_DEFINED__
#define __IEventIsCacheable_FWD_DEFINED__
typedef interface IEventIsCacheable IEventIsCacheable;
#endif 	/* __IEventIsCacheable_FWD_DEFINED__ */


#ifndef __IEventCreateOptions_FWD_DEFINED__
#define __IEventCreateOptions_FWD_DEFINED__
typedef interface IEventCreateOptions IEventCreateOptions;
#endif 	/* __IEventCreateOptions_FWD_DEFINED__ */


#ifndef __IEventDispatcherChain_FWD_DEFINED__
#define __IEventDispatcherChain_FWD_DEFINED__
typedef interface IEventDispatcherChain IEventDispatcherChain;
#endif 	/* __IEventDispatcherChain_FWD_DEFINED__ */


#ifndef __CSEORegDictionary_FWD_DEFINED__
#define __CSEORegDictionary_FWD_DEFINED__

#ifdef __cplusplus
typedef class CSEORegDictionary CSEORegDictionary;
#else
typedef struct CSEORegDictionary CSEORegDictionary;
#endif /* __cplusplus */

#endif 	/* __CSEORegDictionary_FWD_DEFINED__ */


#ifndef __CSEOMimeDictionary_FWD_DEFINED__
#define __CSEOMimeDictionary_FWD_DEFINED__

#ifdef __cplusplus
typedef class CSEOMimeDictionary CSEOMimeDictionary;
#else
typedef struct CSEOMimeDictionary CSEOMimeDictionary;
#endif /* __cplusplus */

#endif 	/* __CSEOMimeDictionary_FWD_DEFINED__ */


#ifndef __CSEOMemDictionary_FWD_DEFINED__
#define __CSEOMemDictionary_FWD_DEFINED__

#ifdef __cplusplus
typedef class CSEOMemDictionary CSEOMemDictionary;
#else
typedef struct CSEOMemDictionary CSEOMemDictionary;
#endif /* __cplusplus */

#endif 	/* __CSEOMemDictionary_FWD_DEFINED__ */


#ifndef __CSEOMetaDictionary_FWD_DEFINED__
#define __CSEOMetaDictionary_FWD_DEFINED__

#ifdef __cplusplus
typedef class CSEOMetaDictionary CSEOMetaDictionary;
#else
typedef struct CSEOMetaDictionary CSEOMetaDictionary;
#endif /* __cplusplus */

#endif 	/* __CSEOMetaDictionary_FWD_DEFINED__ */


#ifndef __CSEODictionaryItem_FWD_DEFINED__
#define __CSEODictionaryItem_FWD_DEFINED__

#ifdef __cplusplus
typedef class CSEODictionaryItem CSEODictionaryItem;
#else
typedef struct CSEODictionaryItem CSEODictionaryItem;
#endif /* __cplusplus */

#endif 	/* __CSEODictionaryItem_FWD_DEFINED__ */


#ifndef __CSEORouter_FWD_DEFINED__
#define __CSEORouter_FWD_DEFINED__

#ifdef __cplusplus
typedef class CSEORouter CSEORouter;
#else
typedef struct CSEORouter CSEORouter;
#endif /* __cplusplus */

#endif 	/* __CSEORouter_FWD_DEFINED__ */


#ifndef __CEventLock_FWD_DEFINED__
#define __CEventLock_FWD_DEFINED__

#ifdef __cplusplus
typedef class CEventLock CEventLock;
#else
typedef struct CEventLock CEventLock;
#endif /* __cplusplus */

#endif 	/* __CEventLock_FWD_DEFINED__ */


#ifndef __CSEOStream_FWD_DEFINED__
#define __CSEOStream_FWD_DEFINED__

#ifdef __cplusplus
typedef class CSEOStream CSEOStream;
#else
typedef struct CSEOStream CSEOStream;
#endif /* __cplusplus */

#endif 	/* __CSEOStream_FWD_DEFINED__ */


#ifndef __CEventManager_FWD_DEFINED__
#define __CEventManager_FWD_DEFINED__

#ifdef __cplusplus
typedef class CEventManager CEventManager;
#else
typedef struct CEventManager CEventManager;
#endif /* __cplusplus */

#endif 	/* __CEventManager_FWD_DEFINED__ */


#ifndef __CEventBindingManager_FWD_DEFINED__
#define __CEventBindingManager_FWD_DEFINED__

#ifdef __cplusplus
typedef class CEventBindingManager CEventBindingManager;
#else
typedef struct CEventBindingManager CEventBindingManager;
#endif /* __cplusplus */

#endif 	/* __CEventBindingManager_FWD_DEFINED__ */


#ifndef __CSEOGenericMoniker_FWD_DEFINED__
#define __CSEOGenericMoniker_FWD_DEFINED__

#ifdef __cplusplus
typedef class CSEOGenericMoniker CSEOGenericMoniker;
#else
typedef struct CSEOGenericMoniker CSEOGenericMoniker;
#endif /* __cplusplus */

#endif 	/* __CSEOGenericMoniker_FWD_DEFINED__ */


#ifndef __CEventMetabaseDatabaseManager_FWD_DEFINED__
#define __CEventMetabaseDatabaseManager_FWD_DEFINED__

#ifdef __cplusplus
typedef class CEventMetabaseDatabaseManager CEventMetabaseDatabaseManager;
#else
typedef struct CEventMetabaseDatabaseManager CEventMetabaseDatabaseManager;
#endif /* __cplusplus */

#endif 	/* __CEventMetabaseDatabaseManager_FWD_DEFINED__ */


#ifndef __CEventUtil_FWD_DEFINED__
#define __CEventUtil_FWD_DEFINED__

#ifdef __cplusplus
typedef class CEventUtil CEventUtil;
#else
typedef struct CEventUtil CEventUtil;
#endif /* __cplusplus */

#endif 	/* __CEventUtil_FWD_DEFINED__ */


#ifndef __CEventComCat_FWD_DEFINED__
#define __CEventComCat_FWD_DEFINED__

#ifdef __cplusplus
typedef class CEventComCat CEventComCat;
#else
typedef struct CEventComCat CEventComCat;
#endif /* __cplusplus */

#endif 	/* __CEventComCat_FWD_DEFINED__ */


#ifndef __CEventRouter_FWD_DEFINED__
#define __CEventRouter_FWD_DEFINED__

#ifdef __cplusplus
typedef class CEventRouter CEventRouter;
#else
typedef struct CEventRouter CEventRouter;
#endif /* __cplusplus */

#endif 	/* __CEventRouter_FWD_DEFINED__ */


/* header files for imported files */
#include "wtypes.h"
#include "ocidl.h"

#ifdef __cplusplus
extern "C"{
#endif 


/* interface __MIDL_itf_seo_0000_0000 */
/* [local] */ 

/*++

Copyright (c) 1999  Microsoft Corporation

Module Name:

     seo.idl / seo.h

Abstract:

     This module contains definitions for the COM interface for
     Server Extension Objects.


--*/
#ifndef SEODLLIMPORT
     #define SEODLLIMPORT _declspec(dllimport)
#endif
#ifndef SEODLLEXPORT
     #define SEODLLEXPORT _declspec(dllexport)
#endif
#ifndef SEODLLDEF
     #ifndef SEODLL_IMPLEMENTATION
             #define SEODLLDEF EXTERN_C SEODLLIMPORT
     #else
             #define SEODLLDEF EXTERN_C SEODLLEXPORT
     #endif
#endif
#define BD_OBJECT                    "Object"
#define BD_PROGID                    "ProgID"
#define BD_PRIORITY                  "Priority"
#define BD_RULEENGINE                "RuleEngine"
#define BD_EXCLUSIVE                 "Exclusive"
#define BD_BINDINGS                  "Bindings"
#define BD_DISPATCHER                "Dispatcher"
#define BD_BINDINGPOINTS             "BindingPoints"
#define BD_RULE                              "Rule"
#define PRIO_HIGHEST                 0
#define PRIO_HIGH                    8191
#define PRIO_MEDIUM                  16383
#define PRIO_LOW                     24575
#define PRIO_LOWEST                  32767
#define PRIO_DEFAULT                 PRIO_LOW
#define PRIO_HIGHEST_STR             L"PRIO_HIGHEST"
#define PRIO_HIGH_STR                L"PRIO_HIGH"
#define PRIO_MEDIUM_STR              L"PRIO_MEDIUM"
#define PRIO_LOW_STR                 L"PRIO_LOW"
#define PRIO_LOWEST_STR              L"PRIO_LOWEST"
#define PRIO_DEFAULT_STR             L"PRIO_DEFAULT"
#define PRIO_MIN                     PRIO_HIGHEST
#define PRIO_MAX                     PRIO_LOWEST


extern RPC_IF_HANDLE __MIDL_itf_seo_0000_0000_v0_0_c_ifspec;
extern RPC_IF_HANDLE __MIDL_itf_seo_0000_0000_v0_0_s_ifspec;

#ifndef __ISEODictionaryItem_INTERFACE_DEFINED__
#define __ISEODictionaryItem_INTERFACE_DEFINED__

/* interface ISEODictionaryItem */
/* [uuid][unique][object][hidden][helpstring][dual] */ 


EXTERN_C const IID IID_ISEODictionaryItem;

#if defined(__cplusplus) && !defined(CINTERFACE)
    
    MIDL_INTERFACE("16d63630-83ae-11d0-a9e3-00aa00685c74")
    ISEODictionaryItem : public IDispatch
    {
    public:
        virtual /* [id][propget][helpstring] */ HRESULT STDMETHODCALLTYPE get_Value( 
            /* [optional][in] */ VARIANT *pvarIndex,
            /* [retval][out] */ VARIANT *pvarResult) = 0;
        
        virtual /* [helpstring] */ HRESULT STDMETHODCALLTYPE AddValue( 
            /* [in] */ VARIANT *pvarIndex,
            /* [in] */ VARIANT *pvarValue) = 0;
        
        virtual /* [helpstring] */ HRESULT STDMETHODCALLTYPE DeleteValue( 
            /* [in] */ VARIANT *pvarIndex) = 0;
        
        virtual /* [propget][helpstring] */ HRESULT STDMETHODCALLTYPE get_Count( 
            /* [retval][out] */ VARIANT *pvarResult) = 0;
        
        virtual /* [helpstring] */ HRESULT STDMETHODCALLTYPE GetStringA( 
            /* [in] */ DWORD dwIndex,
            /* [out][in] */ DWORD *pchCount,
            /* [size_is][out] */ LPSTR pszResult) = 0;
        
        virtual /* [helpstring] */ HRESULT STDMETHODCALLTYPE GetStringW( 
            /* [in] */ DWORD dwIndex,
            /* [out][in] */ DWORD *pchCount,
            /* [size_is][out] */ LPWSTR pszResult) = 0;
        
        virtual /* [helpstring] */ HRESULT STDMETHODCALLTYPE AddStringA( 
            /* [in] */ DWORD dwIndex,
            /* [in] */ LPCSTR pszValue) = 0;
        
        virtual /* [helpstring] */ HRESULT STDMETHODCALLTYPE AddStringW( 
            /* [in] */ DWORD dwIndex,
            /* [in] */ LPCWSTR pszValue) = 0;
        
    };
    
#else 	/* C style interface */

    typedef struct ISEODictionaryItemVtbl
    {
        BEGIN_INTERFACE
        
        HRESULT ( STDMETHODCALLTYPE *QueryInterface )( 
            ISEODictionaryItem * This,
            /* [in] */ REFIID riid,
            /* [annotation][iid_is][out] */ 
            __RPC__deref_out  void **ppvObject);
        
        ULONG ( STDMETHODCALLTYPE *AddRef )( 
            ISEODictionaryItem * This);
        
        ULONG ( STDMETHODCALLTYPE *Release )( 
            ISEODictionaryItem * This);
        
        HRESULT ( STDMETHODCALLTYPE *GetTypeInfoCount )( 
            ISEODictionaryItem * This,
            /* [out] */ UINT *pctinfo);
        
        HRESULT ( STDMETHODCALLTYPE *GetTypeInfo )( 
            ISEODictionaryItem * This,
            /* [in] */ UINT iTInfo,
            /* [in] */ LCID lcid,
            /* [out] */ ITypeInfo **ppTInfo);
        
        HRESULT ( STDMETHODCALLTYPE *GetIDsOfNames )( 
            ISEODictionaryItem * This,
            /* [in] */ REFIID riid,
            /* [size_is][in] */ LPOLESTR *rgszNames,
            /* [range][in] */ UINT cNames,
            /* [in] */ LCID lcid,
            /* [size_is][out] */ DISPID *rgDispId);
        
        /* [local] */ HRESULT ( STDMETHODCALLTYPE *Invoke )( 
            ISEODictionaryItem * This,
            /* [in] */ DISPID dispIdMember,
            /* [in] */ REFIID riid,
            /* [in] */ LCID lcid,
            /* [in] */ WORD wFlags,
            /* [out][in] */ DISPPARAMS *pDispParams,
            /* [out] */ VARIANT *pVarResult,
            /* [out] */ EXCEPINFO *pExcepInfo,
            /* [out] */ UINT *puArgErr);
        
        /* [id][propget][helpstring] */ HRESULT ( STDMETHODCALLTYPE *get_Value )( 
            ISEODictionaryItem * This,
            /* [optional][in] */ VARIANT *pvarIndex,
            /* [retval][out] */ VARIANT *pvarResult);
        
        /* [helpstring] */ HRESULT ( STDMETHODCALLTYPE *AddValue )( 
            ISEODictionaryItem * This,
            /* [in] */ VARIANT *pvarIndex,
            /* [in] */ VARIANT *pvarValue);
        
        /* [helpstring] */ HRESULT ( STDMETHODCALLTYPE *DeleteValue )( 
            ISEODictionaryItem * This,
            /* [in] */ VARIANT *pvarIndex);
        
        /* [propget][helpstring] */ HRESULT ( STDMETHODCALLTYPE *get_Count )( 
            ISEODictionaryItem * This,
            /* [retval][out] */ VARIANT *pvarResult);
        
        /* [helpstring] */ HRESULT ( STDMETHODCALLTYPE *GetStringA )( 
            ISEODictionaryItem * This,
            /* [in] */ DWORD dwIndex,
            /* [out][in] */ DWORD *pchCount,
            /* [size_is][out] */ LPSTR pszResult);
        
        /* [helpstring] */ HRESULT ( STDMETHODCALLTYPE *GetStringW )( 
            ISEODictionaryItem * This,
            /* [in] */ DWORD dwIndex,
            /* [out][in] */ DWORD *pchCount,
            /* [size_is][out] */ LPWSTR pszResult);
        
        /* [helpstring] */ HRESULT ( STDMETHODCALLTYPE *AddStringA )( 
            ISEODictionaryItem * This,
            /* [in] */ DWORD dwIndex,
            /* [in] */ LPCSTR pszValue);
        
        /* [helpstring] */ HRESULT ( STDMETHODCALLTYPE *AddStringW )( 
            ISEODictionaryItem * This,
            /* [in] */ DWORD dwIndex,
            /* [in] */ LPCWSTR pszValue);
        
        END_INTERFACE
    } ISEODictionaryItemVtbl;

    interface ISEODictionaryItem
    {
        CONST_VTBL struct ISEODictionaryItemVtbl *lpVtbl;
    };

    

#ifdef COBJMACROS


#define ISEODictionaryItem_QueryInterface(This,riid,ppvObject)	\
    ( (This)->lpVtbl -> QueryInterface(This,riid,ppvObject) ) 

#define ISEODictionaryItem_AddRef(This)	\
    ( (This)->lpVtbl -> AddRef(This) ) 

#define ISEODictionaryItem_Release(This)	\
    ( (This)->lpVtbl -> Release(This) ) 


#define ISEODictionaryItem_GetTypeInfoCount(This,pctinfo)	\
    ( (This)->lpVtbl -> GetTypeInfoCount(This,pctinfo) ) 

#define ISEODictionaryItem_GetTypeInfo(This,iTInfo,lcid,ppTInfo)	\
    ( (This)->lpVtbl -> GetTypeInfo(This,iTInfo,lcid,ppTInfo) ) 

#define ISEODictionaryItem_GetIDsOfNames(This,riid,rgszNames,cNames,lcid,rgDispId)	\
    ( (This)->lpVtbl -> GetIDsOfNames(This,riid,rgszNames,cNames,lcid,rgDispId) ) 

#define ISEODictionaryItem_Invoke(This,dispIdMember,riid,lcid,wFlags,pDispParams,pVarResult,pExcepInfo,puArgErr)	\
    ( (This)->lpVtbl -> Invoke(This,dispIdMember,riid,lcid,wFlags,pDispParams,pVarResult,pExcepInfo,puArgErr) ) 


#define ISEODictionaryItem_get_Value(This,pvarIndex,pvarResult)	\
    ( (This)->lpVtbl -> get_Value(This,pvarIndex,pvarResult) ) 

#define ISEODictionaryItem_AddValue(This,pvarIndex,pvarValue)	\
    ( (This)->lpVtbl -> AddValue(This,pvarIndex,pvarValue) ) 

#define ISEODictionaryItem_DeleteValue(This,pvarIndex)	\
    ( (This)->lpVtbl -> DeleteValue(This,pvarIndex) ) 

#define ISEODictionaryItem_get_Count(This,pvarResult)	\
    ( (This)->lpVtbl -> get_Count(This,pvarResult) ) 

#define ISEODictionaryItem_GetStringA(This,dwIndex,pchCount,pszResult)	\
    ( (This)->lpVtbl -> GetStringA(This,dwIndex,pchCount,pszResult) ) 

#define ISEODictionaryItem_GetStringW(This,dwIndex,pchCount,pszResult)	\
    ( (This)->lpVtbl -> GetStringW(This,dwIndex,pchCount,pszResult) ) 

#define ISEODictionaryItem_AddStringA(This,dwIndex,pszValue)	\
    ( (This)->lpVtbl -> AddStringA(This,dwIndex,pszValue) ) 

#define ISEODictionaryItem_AddStringW(This,dwIndex,pszValue)	\
    ( (This)->lpVtbl -> AddStringW(This,dwIndex,pszValue) ) 

#endif /* COBJMACROS */


#endif 	/* C style interface */




#endif 	/* __ISEODictionaryItem_INTERFACE_DEFINED__ */


#ifndef __ISEODictionary_INTERFACE_DEFINED__
#define __ISEODictionary_INTERFACE_DEFINED__

/* interface ISEODictionary */
/* [uuid][unique][object][hidden][helpstring][dual] */ 


EXTERN_C const IID IID_ISEODictionary;

#if defined(__cplusplus) && !defined(CINTERFACE)
    
    MIDL_INTERFACE("d8177b40-7bac-11d0-a9e0-00aa00685c74")
    ISEODictionary : public IDispatch
    {
    public:
        virtual /* [propget][id][helpstring] */ HRESULT STDMETHODCALLTYPE get_Item( 
            /* [in] */ VARIANT *pvarName,
            /* [retval][out] */ VARIANT *pvarResult) = 0;
        
        virtual /* [propput][helpstring] */ HRESULT STDMETHODCALLTYPE put_Item( 
            /* [in] */ VARIANT *pvarName,
            /* [in] */ VARIANT *pvarValue) = 0;
        
        virtual /* [hidden][propget][id][helpstring] */ HRESULT STDMETHODCALLTYPE get__NewEnum( 
            /* [retval][out] */ IUnknown **ppunkResult) = 0;
        
        virtual /* [helpstring] */ HRESULT STDMETHODCALLTYPE GetVariantA( 
            /* [in] */ LPCSTR pszName,
            /* [retval][out] */ VARIANT *pvarResult) = 0;
        
        virtual /* [helpstring] */ HRESULT STDMETHODCALLTYPE GetVariantW( 
            /* [in] */ LPCWSTR pszName,
            /* [retval][out] */ VARIANT *pvarResult) = 0;
        
        virtual /* [helpstring] */ HRESULT STDMETHODCALLTYPE SetVariantA( 
            /* [in] */ LPCSTR pszName,
            /* [in] */ VARIANT *pvarValue) = 0;
        
        virtual /* [helpstring] */ HRESULT STDMETHODCALLTYPE SetVariantW( 
            /* [in] */ LPCWSTR pszName,
            /* [in] */ VARIANT *pvarValue) = 0;
        
        virtual /* [helpstring] */ HRESULT STDMETHODCALLTYPE GetStringA( 
            /* [in] */ LPCSTR pszName,
            /* [out][in] */ DWORD *pchCount,
            /* [size_is][out] */ LPSTR pszResult) = 0;
        
        virtual /* [helpstring] */ HRESULT STDMETHODCALLTYPE GetStringW( 
            /* [in] */ LPCWSTR pszName,
            /* [out][in] */ DWORD *pchCount,
            /* [size_is][out] */ LPWSTR pszResult) = 0;
        
        virtual /* [helpstring] */ HRESULT STDMETHODCALLTYPE SetStringA( 
            /* [in] */ LPCSTR pszName,
            /* [in] */ DWORD chCount,
            /* [size_is][in] */ LPCSTR pszValue) = 0;
        
        virtual /* [helpstring] */ HRESULT STDMETHODCALLTYPE SetStringW( 
            /* [in] */ LPCWSTR pszName,
            /* [in] */ DWORD chCount,
            /* [size_is][in] */ LPCWSTR pszValue) = 0;
        
        virtual /* [helpstring] */ HRESULT STDMETHODCALLTYPE GetDWordA( 
            /* [in] */ LPCSTR pszName,
            /* [retval][out] */ DWORD *pdwResult) = 0;
        
        virtual /* [helpstring] */ HRESULT STDMETHODCALLTYPE GetDWordW( 
            /* [in] */ LPCWSTR pszName,
            /* [retval][out] */ DWORD *pdwResult) = 0;
        
        virtual /* [helpstring] */ HRESULT STDMETHODCALLTYPE SetDWordA( 
            /* [in] */ LPCSTR pszName,
            /* [in] */ DWORD dwValue) = 0;
        
        virtual /* [helpstring] */ HRESULT STDMETHODCALLTYPE SetDWordW( 
            /* [in] */ LPCWSTR pszName,
            /* [in] */ DWORD dwValue) = 0;
        
        virtual /* [helpstring] */ HRESULT STDMETHODCALLTYPE GetInterfaceA( 
            /* [in] */ LPCSTR pszName,
            /* [in] */ REFIID iidDesired,
            /* [retval][iid_is][out] */ IUnknown **ppunkResult) = 0;
        
        virtual /* [helpstring] */ HRESULT STDMETHODCALLTYPE GetInterfaceW( 
            /* [in] */ LPCWSTR pszName,
            /* [in] */ REFIID iidDesired,
            /* [retval][iid_is][out] */ IUnknown **ppunkResult) = 0;
        
        virtual /* [helpstring] */ HRESULT STDMETHODCALLTYPE SetInterfaceA( 
            /* [in] */ LPCSTR pszName,
            /* [unique][in] */ IUnknown *punkValue) = 0;
        
        virtual /* [helpstring] */ HRESULT STDMETHODCALLTYPE SetInterfaceW( 
            /* [in] */ LPCWSTR pszName,
            /* [unique][in] */ IUnknown *punkValue) = 0;
        
    };
    
#else 	/* C style interface */

    typedef struct ISEODictionaryVtbl
    {
        BEGIN_INTERFACE
        
        HRESULT ( STDMETHODCALLTYPE *QueryInterface )( 
            ISEODictionary * This,
            /* [in] */ REFIID riid,
            /* [annotation][iid_is][out] */ 
            __RPC__deref_out  void **ppvObject);
        
        ULONG ( STDMETHODCALLTYPE *AddRef )( 
            ISEODictionary * This);
        
        ULONG ( STDMETHODCALLTYPE *Release )( 
            ISEODictionary * This);
        
        HRESULT ( STDMETHODCALLTYPE *GetTypeInfoCount )( 
            ISEODictionary * This,
            /* [out] */ UINT *pctinfo);
        
        HRESULT ( STDMETHODCALLTYPE *GetTypeInfo )( 
            ISEODictionary * This,
            /* [in] */ UINT iTInfo,
            /* [in] */ LCID lcid,
            /* [out] */ ITypeInfo **ppTInfo);
        
        HRESULT ( STDMETHODCALLTYPE *GetIDsOfNames )( 
            ISEODictionary * This,
            /* [in] */ REFIID riid,
            /* [size_is][in] */ LPOLESTR *rgszNames,
            /* [range][in] */ UINT cNames,
            /* [in] */ LCID lcid,
            /* [size_is][out] */ DISPID *rgDispId);
        
        /* [local] */ HRESULT ( STDMETHODCALLTYPE *Invoke )( 
            ISEODictionary * This,
            /* [in] */ DISPID dispIdMember,
            /* [in] */ REFIID riid,
            /* [in] */ LCID lcid,
            /* [in] */ WORD wFlags,
            /* [out][in] */ DISPPARAMS *pDispParams,
            /* [out] */ VARIANT *pVarResult,
            /* [out] */ EXCEPINFO *pExcepInfo,
            /* [out] */ UINT *puArgErr);
        
        /* [propget][id][helpstring] */ HRESULT ( STDMETHODCALLTYPE *get_Item )( 
            ISEODictionary * This,
            /* [in] */ VARIANT *pvarName,
            /* [retval][out] */ VARIANT *pvarResult);
        
        /* [propput][helpstring] */ HRESULT ( STDMETHODCALLTYPE *put_Item )( 
            ISEODictionary * This,
            /* [in] */ VARIANT *pvarName,
            /* [in] */ VARIANT *pvarValue);
        
        /* [hidden][propget][id][helpstring] */ HRESULT ( STDMETHODCALLTYPE *get__NewEnum )( 
            ISEODictionary * This,
            /* [retval][out] */ IUnknown **ppunkResult);
        
        /* [helpstring] */ HRESULT ( STDMETHODCALLTYPE *GetVariantA )( 
            ISEODictionary * This,
            /* [in] */ LPCSTR pszName,
            /* [retval][out] */ VARIANT *pvarResult);
        
        /* [helpstring] */ HRESULT ( STDMETHODCALLTYPE *GetVariantW )( 
            ISEODictionary * This,
            /* [in] */ LPCWSTR pszName,
            /* [retval][out] */ VARIANT *pvarResult);
        
        /* [helpstring] */ HRESULT ( STDMETHODCALLTYPE *SetVariantA )( 
            ISEODictionary * This,
            /* [in] */ LPCSTR pszName,
            /* [in] */ VARIANT *pvarValue);
        
        /* [helpstring] */ HRESULT ( STDMETHODCALLTYPE *SetVariantW )( 
            ISEODictionary * This,
            /* [in] */ LPCWSTR pszName,
            /* [in] */ VARIANT *pvarValue);
        
        /* [helpstring] */ HRESULT ( STDMETHODCALLTYPE *GetStringA )( 
            ISEODictionary * This,
            /* [in] */ LPCSTR pszName,
            /* [out][in] */ DWORD *pchCount,
            /* [size_is][out] */ LPSTR pszResult);
        
        /* [helpstring] */ HRESULT ( STDMETHODCALLTYPE *GetStringW )( 
            ISEODictionary * This,
            /* [in] */ LPCWSTR pszName,
            /* [out][in] */ DWORD *pchCount,
            /* [size_is][out] */ LPWSTR pszResult);
        
        /* [helpstring] */ HRESULT ( STDMETHODCALLTYPE *SetStringA )( 
            ISEODictionary * This,
            /* [in] */ LPCSTR pszName,
            /* [in] */ DWORD chCount,
            /* [size_is][in] */ LPCSTR pszValue);
        
        /* [helpstring] */ HRESULT ( STDMETHODCALLTYPE *SetStringW )( 
            ISEODictionary * This,
            /* [in] */ LPCWSTR pszName,
            /* [in] */ DWORD chCount,
            /* [size_is][in] */ LPCWSTR pszValue);
        
        /* [helpstring] */ HRESULT ( STDMETHODCALLTYPE *GetDWordA )( 
            ISEODictionary * This,
            /* [in] */ LPCSTR pszName,
            /* [retval][out] */ DWORD *pdwResult);
        
        /* [helpstring] */ HRESULT ( STDMETHODCALLTYPE *GetDWordW )( 
            ISEODictionary * This,
            /* [in] */ LPCWSTR pszName,
            /* [retval][out] */ DWORD *pdwResult);
        
        /* [helpstring] */ HRESULT ( STDMETHODCALLTYPE *SetDWordA )( 
            ISEODictionary * This,
            /* [in] */ LPCSTR pszName,
            /* [in] */ DWORD dwValue);
        
        /* [helpstring] */ HRESULT ( STDMETHODCALLTYPE *SetDWordW )( 
            ISEODictionary * This,
            /* [in] */ LPCWSTR pszName,
            /* [in] */ DWORD dwValue);
        
        /* [helpstring] */ HRESULT ( STDMETHODCALLTYPE *GetInterfaceA )( 
            ISEODictionary * This,
            /* [in] */ LPCSTR pszName,
            /* [in] */ REFIID iidDesired,
            /* [retval][iid_is][out] */ IUnknown **ppunkResult);
        
        /* [helpstring] */ HRESULT ( STDMETHODCALLTYPE *GetInterfaceW )( 
            ISEODictionary * This,
            /* [in] */ LPCWSTR pszName,
            /* [in] */ REFIID iidDesired,
            /* [retval][iid_is][out] */ IUnknown **ppunkResult);
        
        /* [helpstring] */ HRESULT ( STDMETHODCALLTYPE *SetInterfaceA )( 
            ISEODictionary * This,
            /* [in] */ LPCSTR pszName,
            /* [unique][in] */ IUnknown *punkValue);
        
        /* [helpstring] */ HRESULT ( STDMETHODCALLTYPE *SetInterfaceW )( 
            ISEODictionary * This,
            /* [in] */ LPCWSTR pszName,
            /* [unique][in] */ IUnknown *punkValue);
        
        END_INTERFACE
    } ISEODictionaryVtbl;

    interface ISEODictionary
    {
        CONST_VTBL struct ISEODictionaryVtbl *lpVtbl;
    };

    

#ifdef COBJMACROS


#define ISEODictionary_QueryInterface(This,riid,ppvObject)	\
    ( (This)->lpVtbl -> QueryInterface(This,riid,ppvObject) ) 

#define ISEODictionary_AddRef(This)	\
    ( (This)->lpVtbl -> AddRef(This) ) 

#define ISEODictionary_Release(This)	\
    ( (This)->lpVtbl -> Release(This) ) 


#define ISEODictionary_GetTypeInfoCount(This,pctinfo)	\
    ( (This)->lpVtbl -> GetTypeInfoCount(This,pctinfo) ) 

#define ISEODictionary_GetTypeInfo(This,iTInfo,lcid,ppTInfo)	\
    ( (This)->lpVtbl -> GetTypeInfo(This,iTInfo,lcid,ppTInfo) ) 

#define ISEODictionary_GetIDsOfNames(This,riid,rgszNames,cNames,lcid,rgDispId)	\
    ( (This)->lpVtbl -> GetIDsOfNames(This,riid,rgszNames,cNames,lcid,rgDispId) ) 

#define ISEODictionary_Invoke(This,dispIdMember,riid,lcid,wFlags,pDispParams,pVarResult,pExcepInfo,puArgErr)	\
    ( (This)->lpVtbl -> Invoke(This,dispIdMember,riid,lcid,wFlags,pDispParams,pVarResult,pExcepInfo,puArgErr) ) 


#define ISEODictionary_get_Item(This,pvarName,pvarResult)	\
    ( (This)->lpVtbl -> get_Item(This,pvarName,pvarResult) ) 

#define ISEODictionary_put_Item(This,pvarName,pvarValue)	\
    ( (This)->lpVtbl -> put_Item(This,pvarName,pvarValue) ) 

#define ISEODictionary_get__NewEnum(This,ppunkResult)	\
    ( (This)->lpVtbl -> get__NewEnum(This,ppunkResult) ) 

#define ISEODictionary_GetVariantA(This,pszName,pvarResult)	\
    ( (This)->lpVtbl -> GetVariantA(This,pszName,pvarResult) ) 

#define ISEODictionary_GetVariantW(This,pszName,pvarResult)	\
    ( (This)->lpVtbl -> GetVariantW(This,pszName,pvarResult) ) 

#define ISEODictionary_SetVariantA(This,pszName,pvarValue)	\
    ( (This)->lpVtbl -> SetVariantA(This,pszName,pvarValue) ) 

#define ISEODictionary_SetVariantW(This,pszName,pvarValue)	\
    ( (This)->lpVtbl -> SetVariantW(This,pszName,pvarValue) ) 

#define ISEODictionary_GetStringA(This,pszName,pchCount,pszResult)	\
    ( (This)->lpVtbl -> GetStringA(This,pszName,pchCount,pszResult) ) 

#define ISEODictionary_GetStringW(This,pszName,pchCount,pszResult)	\
    ( (This)->lpVtbl -> GetStringW(This,pszName,pchCount,pszResult) ) 

#define ISEODictionary_SetStringA(This,pszName,chCount,pszValue)	\
    ( (This)->lpVtbl -> SetStringA(This,pszName,chCount,pszValue) ) 

#define ISEODictionary_SetStringW(This,pszName,chCount,pszValue)	\
    ( (This)->lpVtbl -> SetStringW(This,pszName,chCount,pszValue) ) 

#define ISEODictionary_GetDWordA(This,pszName,pdwResult)	\
    ( (This)->lpVtbl -> GetDWordA(This,pszName,pdwResult) ) 

#define ISEODictionary_GetDWordW(This,pszName,pdwResult)	\
    ( (This)->lpVtbl -> GetDWordW(This,pszName,pdwResult) ) 

#define ISEODictionary_SetDWordA(This,pszName,dwValue)	\
    ( (This)->lpVtbl -> SetDWordA(This,pszName,dwValue) ) 

#define ISEODictionary_SetDWordW(This,pszName,dwValue)	\
    ( (This)->lpVtbl -> SetDWordW(This,pszName,dwValue) ) 

#define ISEODictionary_GetInterfaceA(This,pszName,iidDesired,ppunkResult)	\
    ( (This)->lpVtbl -> GetInterfaceA(This,pszName,iidDesired,ppunkResult) ) 

#define ISEODictionary_GetInterfaceW(This,pszName,iidDesired,ppunkResult)	\
    ( (This)->lpVtbl -> GetInterfaceW(This,pszName,iidDesired,ppunkResult) ) 

#define ISEODictionary_SetInterfaceA(This,pszName,punkValue)	\
    ( (This)->lpVtbl -> SetInterfaceA(This,pszName,punkValue) ) 

#define ISEODictionary_SetInterfaceW(This,pszName,punkValue)	\
    ( (This)->lpVtbl -> SetInterfaceW(This,pszName,punkValue) ) 

#endif /* COBJMACROS */


#endif 	/* C style interface */




#endif 	/* __ISEODictionary_INTERFACE_DEFINED__ */


#ifndef __IEventLock_INTERFACE_DEFINED__
#define __IEventLock_INTERFACE_DEFINED__

/* interface IEventLock */
/* [uuid][hidden][unique][object][helpstring][dual] */ 


EXTERN_C const IID IID_IEventLock;

#if defined(__cplusplus) && !defined(CINTERFACE)
    
    MIDL_INTERFACE("1b7058f0-af88-11d0-a9eb-00aa00685c74")
    IEventLock : public IDispatch
    {
    public:
        virtual /* [helpstring] */ HRESULT STDMETHODCALLTYPE LockRead( 
            /* [in] */ int iTimeoutMS) = 0;
        
        virtual /* [helpstring] */ HRESULT STDMETHODCALLTYPE UnlockRead( void) = 0;
        
        virtual /* [helpstring] */ HRESULT STDMETHODCALLTYPE LockWrite( 
            /* [in] */ int iTimeoutMS) = 0;
        
        virtual /* [helpstring] */ HRESULT STDMETHODCALLTYPE UnlockWrite( void) = 0;
        
    };
    
#else 	/* C style interface */

    typedef struct IEventLockVtbl
    {
        BEGIN_INTERFACE
        
        HRESULT ( STDMETHODCALLTYPE *QueryInterface )( 
            IEventLock * This,
            /* [in] */ REFIID riid,
            /* [annotation][iid_is][out] */ 
            __RPC__deref_out  void **ppvObject);
        
        ULONG ( STDMETHODCALLTYPE *AddRef )( 
            IEventLock * This);
        
        ULONG ( STDMETHODCALLTYPE *Release )( 
            IEventLock * This);
        
        HRESULT ( STDMETHODCALLTYPE *GetTypeInfoCount )( 
            IEventLock * This,
            /* [out] */ UINT *pctinfo);
        
        HRESULT ( STDMETHODCALLTYPE *GetTypeInfo )( 
            IEventLock * This,
            /* [in] */ UINT iTInfo,
            /* [in] */ LCID lcid,
            /* [out] */ ITypeInfo **ppTInfo);
        
        HRESULT ( STDMETHODCALLTYPE *GetIDsOfNames )( 
            IEventLock * This,
            /* [in] */ REFIID riid,
            /* [size_is][in] */ LPOLESTR *rgszNames,
            /* [range][in] */ UINT cNames,
            /* [in] */ LCID lcid,
            /* [size_is][out] */ DISPID *rgDispId);
        
        /* [local] */ HRESULT ( STDMETHODCALLTYPE *Invoke )( 
            IEventLock * This,
            /* [in] */ DISPID dispIdMember,
            /* [in] */ REFIID riid,
            /* [in] */ LCID lcid,
            /* [in] */ WORD wFlags,
            /* [out][in] */ DISPPARAMS *pDispParams,
            /* [out] */ VARIANT *pVarResult,
            /* [out] */ EXCEPINFO *pExcepInfo,
            /* [out] */ UINT *puArgErr);
        
        /* [helpstring] */ HRESULT ( STDMETHODCALLTYPE *LockRead )( 
            IEventLock * This,
            /* [in] */ int iTimeoutMS);
        
        /* [helpstring] */ HRESULT ( STDMETHODCALLTYPE *UnlockRead )( 
            IEventLock * This);
        
        /* [helpstring] */ HRESULT ( STDMETHODCALLTYPE *LockWrite )( 
            IEventLock * This,
            /* [in] */ int iTimeoutMS);
        
        /* [helpstring] */ HRESULT ( STDMETHODCALLTYPE *UnlockWrite )( 
            IEventLock * This);
        
        END_INTERFACE
    } IEventLockVtbl;

    interface IEventLock
    {
        CONST_VTBL struct IEventLockVtbl *lpVtbl;
    };

    

#ifdef COBJMACROS


#define IEventLock_QueryInterface(This,riid,ppvObject)	\
    ( (This)->lpVtbl -> QueryInterface(This,riid,ppvObject) ) 

#define IEventLock_AddRef(This)	\
    ( (This)->lpVtbl -> AddRef(This) ) 

#define IEventLock_Release(This)	\
    ( (This)->lpVtbl -> Release(This) ) 


#define IEventLock_GetTypeInfoCount(This,pctinfo)	\
    ( (This)->lpVtbl -> GetTypeInfoCount(This,pctinfo) ) 

#define IEventLock_GetTypeInfo(This,iTInfo,lcid,ppTInfo)	\
    ( (This)->lpVtbl -> GetTypeInfo(This,iTInfo,lcid,ppTInfo) ) 

#define IEventLock_GetIDsOfNames(This,riid,rgszNames,cNames,lcid,rgDispId)	\
    ( (This)->lpVtbl -> GetIDsOfNames(This,riid,rgszNames,cNames,lcid,rgDispId) ) 

#define IEventLock_Invoke(This,dispIdMember,riid,lcid,wFlags,pDispParams,pVarResult,pExcepInfo,puArgErr)	\
    ( (This)->lpVtbl -> Invoke(This,dispIdMember,riid,lcid,wFlags,pDispParams,pVarResult,pExcepInfo,puArgErr) ) 


#define IEventLock_LockRead(This,iTimeoutMS)	\
    ( (This)->lpVtbl -> LockRead(This,iTimeoutMS) ) 

#define IEventLock_UnlockRead(This)	\
    ( (This)->lpVtbl -> UnlockRead(This) ) 

#define IEventLock_LockWrite(This,iTimeoutMS)	\
    ( (This)->lpVtbl -> LockWrite(This,iTimeoutMS) ) 

#define IEventLock_UnlockWrite(This)	\
    ( (This)->lpVtbl -> UnlockWrite(This) ) 

#endif /* COBJMACROS */


#endif 	/* C style interface */




#endif 	/* __IEventLock_INTERFACE_DEFINED__ */


#ifndef __ISEORouter_INTERFACE_DEFINED__
#define __ISEORouter_INTERFACE_DEFINED__

/* interface ISEORouter */
/* [uuid][unique][object][hidden][helpstring] */ 


EXTERN_C const IID IID_ISEORouter;

#if defined(__cplusplus) && !defined(CINTERFACE)
    
    MIDL_INTERFACE("2b6ac0f0-7e03-11d0-a9e0-00aa00685c74")
    ISEORouter : public IUnknown
    {
    public:
        virtual /* [propget][helpstring] */ HRESULT STDMETHODCALLTYPE get_Database( 
            /* [retval][out] */ ISEODictionary **ppdictResult) = 0;
        
        virtual /* [propput][helpstring] */ HRESULT STDMETHODCALLTYPE put_Database( 
            /* [unique][in] */ ISEODictionary *pdictDatabase) = 0;
        
        virtual /* [propget][helpstring] */ HRESULT STDMETHODCALLTYPE get_Server( 
            /* [retval][out] */ ISEODictionary **ppdictResult) = 0;
        
        virtual /* [propput][helpstring] */ HRESULT STDMETHODCALLTYPE put_Server( 
            /* [unique][in] */ ISEODictionary *pdictServer) = 0;
        
        virtual /* [propget][helpstring] */ HRESULT STDMETHODCALLTYPE get_Applications( 
            /* [retval][out] */ ISEODictionary **ppdictResult) = 0;
        
        virtual /* [helpstring] */ HRESULT STDMETHODCALLTYPE GetDispatcher( 
            /* [in] */ REFIID iidEvent,
            /* [in] */ REFIID iidDesired,
            /* [retval][iid_is][out] */ IUnknown **ppUnkResult) = 0;
        
        virtual /* [helpstring] */ HRESULT STDMETHODCALLTYPE GetDispatcherByCLSID( 
            /* [in] */ REFCLSID clsidDispatcher,
            /* [in] */ REFIID iidEvent,
            /* [in] */ REFIID iidDesired,
            /* [retval][iid_is][out] */ IUnknown **ppUnkResult) = 0;
        
    };
    
#else 	/* C style interface */

    typedef struct ISEORouterVtbl
    {
        BEGIN_INTERFACE
        
        HRESULT ( STDMETHODCALLTYPE *QueryInterface )( 
            ISEORouter * This,
            /* [in] */ REFIID riid,
            /* [annotation][iid_is][out] */ 
            __RPC__deref_out  void **ppvObject);
        
        ULONG ( STDMETHODCALLTYPE *AddRef )( 
            ISEORouter * This);
        
        ULONG ( STDMETHODCALLTYPE *Release )( 
            ISEORouter * This);
        
        /* [propget][helpstring] */ HRESULT ( STDMETHODCALLTYPE *get_Database )( 
            ISEORouter * This,
            /* [retval][out] */ ISEODictionary **ppdictResult);
        
        /* [propput][helpstring] */ HRESULT ( STDMETHODCALLTYPE *put_Database )( 
            ISEORouter * This,
            /* [unique][in] */ ISEODictionary *pdictDatabase);
        
        /* [propget][helpstring] */ HRESULT ( STDMETHODCALLTYPE *get_Server )( 
            ISEORouter * This,
            /* [retval][out] */ ISEODictionary **ppdictResult);
        
        /* [propput][helpstring] */ HRESULT ( STDMETHODCALLTYPE *put_Server )( 
            ISEORouter * This,
            /* [unique][in] */ ISEODictionary *pdictServer);
        
        /* [propget][helpstring] */ HRESULT ( STDMETHODCALLTYPE *get_Applications )( 
            ISEORouter * This,
            /* [retval][out] */ ISEODictionary **ppdictResult);
        
        /* [helpstring] */ HRESULT ( STDMETHODCALLTYPE *GetDispatcher )( 
            ISEORouter * This,
            /* [in] */ REFIID iidEvent,
            /* [in] */ REFIID iidDesired,
            /* [retval][iid_is][out] */ IUnknown **ppUnkResult);
        
        /* [helpstring] */ HRESULT ( STDMETHODCALLTYPE *GetDispatcherByCLSID )( 
            ISEORouter * This,
            /* [in] */ REFCLSID clsidDispatcher,
            /* [in] */ REFIID iidEvent,
            /* [in] */ REFIID iidDesired,
            /* [retval][iid_is][out] */ IUnknown **ppUnkResult);
        
        END_INTERFACE
    } ISEORouterVtbl;

    interface ISEORouter
    {
        CONST_VTBL struct ISEORouterVtbl *lpVtbl;
    };

    

#ifdef COBJMACROS


#define ISEORouter_QueryInterface(This,riid,ppvObject)	\
    ( (This)->lpVtbl -> QueryInterface(This,riid,ppvObject) ) 

#define ISEORouter_AddRef(This)	\
    ( (This)->lpVtbl -> AddRef(This) ) 

#define ISEORouter_Release(This)	\
    ( (This)->lpVtbl -> Release(This) ) 


#define ISEORouter_get_Database(This,ppdictResult)	\
    ( (This)->lpVtbl -> get_Database(This,ppdictResult) ) 

#define ISEORouter_put_Database(This,pdictDatabase)	\
    ( (This)->lpVtbl -> put_Database(This,pdictDatabase) ) 

#define ISEORouter_get_Server(This,ppdictResult)	\
    ( (This)->lpVtbl -> get_Server(This,ppdictResult) ) 

#define ISEORouter_put_Server(This,pdictServer)	\
    ( (This)->lpVtbl -> put_Server(This,pdictServer) ) 

#define ISEORouter_get_Applications(This,ppdictResult)	\
    ( (This)->lpVtbl -> get_Applications(This,ppdictResult) ) 

#define ISEORouter_GetDispatcher(This,iidEvent,iidDesired,ppUnkResult)	\
    ( (This)->lpVtbl -> GetDispatcher(This,iidEvent,iidDesired,ppUnkResult) ) 

#define ISEORouter_GetDispatcherByCLSID(This,clsidDispatcher,iidEvent,iidDesired,ppUnkResult)	\
    ( (This)->lpVtbl -> GetDispatcherByCLSID(This,clsidDispatcher,iidEvent,iidDesired,ppUnkResult) ) 

#endif /* COBJMACROS */


#endif 	/* C style interface */




#endif 	/* __ISEORouter_INTERFACE_DEFINED__ */


#ifndef __IMCISMessageFilter_INTERFACE_DEFINED__
#define __IMCISMessageFilter_INTERFACE_DEFINED__

/* interface IMCISMessageFilter */
/* [uuid][unique][object][hidden][helpstring] */ 


EXTERN_C const IID IID_IMCISMessageFilter;

#if defined(__cplusplus) && !defined(CINTERFACE)
    
    MIDL_INTERFACE("f174e5b0-9046-11d0-a9e8-00aa00685c74")
    IMCISMessageFilter : public IUnknown
    {
    public:
        virtual HRESULT STDMETHODCALLTYPE OnMessage( 
            /* [unique][in] */ IStream *pstreamMessage,
            /* [unique][in] */ ISEODictionary *pdictEnvelope,
            /* [unique][in] */ ISEODictionary *pdictBinding) = 0;
        
    };
    
#else 	/* C style interface */

    typedef struct IMCISMessageFilterVtbl
    {
        BEGIN_INTERFACE
        
        HRESULT ( STDMETHODCALLTYPE *QueryInterface )( 
            IMCISMessageFilter * This,
            /* [in] */ REFIID riid,
            /* [annotation][iid_is][out] */ 
            __RPC__deref_out  void **ppvObject);
        
        ULONG ( STDMETHODCALLTYPE *AddRef )( 
            IMCISMessageFilter * This);
        
        ULONG ( STDMETHODCALLTYPE *Release )( 
            IMCISMessageFilter * This);
        
        HRESULT ( STDMETHODCALLTYPE *OnMessage )( 
            IMCISMessageFilter * This,
            /* [unique][in] */ IStream *pstreamMessage,
            /* [unique][in] */ ISEODictionary *pdictEnvelope,
            /* [unique][in] */ ISEODictionary *pdictBinding);
        
        END_INTERFACE
    } IMCISMessageFilterVtbl;

    interface IMCISMessageFilter
    {
        CONST_VTBL struct IMCISMessageFilterVtbl *lpVtbl;
    };

    

#ifdef COBJMACROS


#define IMCISMessageFilter_QueryInterface(This,riid,ppvObject)	\
    ( (This)->lpVtbl -> QueryInterface(This,riid,ppvObject) ) 

#define IMCISMessageFilter_AddRef(This)	\
    ( (This)->lpVtbl -> AddRef(This) ) 

#define IMCISMessageFilter_Release(This)	\
    ( (This)->lpVtbl -> Release(This) ) 


#define IMCISMessageFilter_OnMessage(This,pstreamMessage,pdictEnvelope,pdictBinding)	\
    ( (This)->lpVtbl -> OnMessage(This,pstreamMessage,pdictEnvelope,pdictBinding) ) 

#endif /* COBJMACROS */


#endif 	/* C style interface */




#endif 	/* __IMCISMessageFilter_INTERFACE_DEFINED__ */


#ifndef __ISEOBindingRuleEngine_INTERFACE_DEFINED__
#define __ISEOBindingRuleEngine_INTERFACE_DEFINED__

/* interface ISEOBindingRuleEngine */
/* [uuid][unique][object][hidden][helpstring] */ 


EXTERN_C const IID IID_ISEOBindingRuleEngine;

#if defined(__cplusplus) && !defined(CINTERFACE)
    
    MIDL_INTERFACE("f2d1daf0-2236-11d0-a9ce-00aa00685c74")
    ISEOBindingRuleEngine : public IUnknown
    {
    public:
        virtual /* [helpstring] */ HRESULT STDMETHODCALLTYPE Evaluate( 
            /* [unique][in] */ ISEODictionary *pdictEvent,
            /* [unique][in] */ ISEODictionary *pdictBinding) = 0;
        
    };
    
#else 	/* C style interface */

    typedef struct ISEOBindingRuleEngineVtbl
    {
        BEGIN_INTERFACE
        
        HRESULT ( STDMETHODCALLTYPE *QueryInterface )( 
            ISEOBindingRuleEngine * This,
            /* [in] */ REFIID riid,
            /* [annotation][iid_is][out] */ 
            __RPC__deref_out  void **ppvObject);
        
        ULONG ( STDMETHODCALLTYPE *AddRef )( 
            ISEOBindingRuleEngine * This);
        
        ULONG ( STDMETHODCALLTYPE *Release )( 
            ISEOBindingRuleEngine * This);
        
        /* [helpstring] */ HRESULT ( STDMETHODCALLTYPE *Evaluate )( 
            ISEOBindingRuleEngine * This,
            /* [unique][in] */ ISEODictionary *pdictEvent,
            /* [unique][in] */ ISEODictionary *pdictBinding);
        
        END_INTERFACE
    } ISEOBindingRuleEngineVtbl;

    interface ISEOBindingRuleEngine
    {
        CONST_VTBL struct ISEOBindingRuleEngineVtbl *lpVtbl;
    };

    

#ifdef COBJMACROS


#define ISEOBindingRuleEngine_QueryInterface(This,riid,ppvObject)	\
    ( (This)->lpVtbl -> QueryInterface(This,riid,ppvObject) ) 

#define ISEOBindingRuleEngine_AddRef(This)	\
    ( (This)->lpVtbl -> AddRef(This) ) 

#define ISEOBindingRuleEngine_Release(This)	\
    ( (This)->lpVtbl -> Release(This) ) 


#define ISEOBindingRuleEngine_Evaluate(This,pdictEvent,pdictBinding)	\
    ( (This)->lpVtbl -> Evaluate(This,pdictEvent,pdictBinding) ) 

#endif /* COBJMACROS */


#endif 	/* C style interface */




#endif 	/* __ISEOBindingRuleEngine_INTERFACE_DEFINED__ */


#ifndef __ISEOEventSink_INTERFACE_DEFINED__
#define __ISEOEventSink_INTERFACE_DEFINED__

/* interface ISEOEventSink */
/* [uuid][unique][object][hidden][helpstring] */ 


EXTERN_C const IID IID_ISEOEventSink;

#if defined(__cplusplus) && !defined(CINTERFACE)
    
    MIDL_INTERFACE("1cab4c20-94f4-11d0-a9e8-00aa00685c74")
    ISEOEventSink : public IUnknown
    {
    public:
        virtual /* [helpstring] */ HRESULT STDMETHODCALLTYPE OnEvent( void) = 0;
        
    };
    
#else 	/* C style interface */

    typedef struct ISEOEventSinkVtbl
    {
        BEGIN_INTERFACE
        
        HRESULT ( STDMETHODCALLTYPE *QueryInterface )( 
            ISEOEventSink * This,
            /* [in] */ REFIID riid,
            /* [annotation][iid_is][out] */ 
            __RPC__deref_out  void **ppvObject);
        
        ULONG ( STDMETHODCALLTYPE *AddRef )( 
            ISEOEventSink * This);
        
        ULONG ( STDMETHODCALLTYPE *Release )( 
            ISEOEventSink * This);
        
        /* [helpstring] */ HRESULT ( STDMETHODCALLTYPE *OnEvent )( 
            ISEOEventSink * This);
        
        END_INTERFACE
    } ISEOEventSinkVtbl;

    interface ISEOEventSink
    {
        CONST_VTBL struct ISEOEventSinkVtbl *lpVtbl;
    };

    

#ifdef COBJMACROS


#define ISEOEventSink_QueryInterface(This,riid,ppvObject)	\
    ( (This)->lpVtbl -> QueryInterface(This,riid,ppvObject) ) 

#define ISEOEventSink_AddRef(This)	\
    ( (This)->lpVtbl -> AddRef(This) ) 

#define ISEOEventSink_Release(This)	\
    ( (This)->lpVtbl -> Release(This) ) 


#define ISEOEventSink_OnEvent(This)	\
    ( (This)->lpVtbl -> OnEvent(This) ) 

#endif /* COBJMACROS */


#endif 	/* C style interface */




#endif 	/* __ISEOEventSink_INTERFACE_DEFINED__ */


#ifndef __ISEORegDictionary_INTERFACE_DEFINED__
#define __ISEORegDictionary_INTERFACE_DEFINED__

/* interface ISEORegDictionary */
/* [uuid][unique][object][helpstring] */ 

typedef SIZE_T SEO_HKEY;


EXTERN_C const IID IID_ISEORegDictionary;

#if defined(__cplusplus) && !defined(CINTERFACE)
    
    MIDL_INTERFACE("347cacb0-2d1e-11d0-a9cf-00aa00685c74")
    ISEORegDictionary : public ISEODictionary
    {
    public:
        virtual /* [helpstring] */ HRESULT STDMETHODCALLTYPE Load( 
            /* [in] */ LPCOLESTR pszMachine,
            /* [in] */ SEO_HKEY skBaseKey,
            /* [in] */ LPCOLESTR pszSubKey,
            /* [unique][in] */ IErrorLog *pErrorLog) = 0;
        
    };
    
#else 	/* C style interface */

    typedef struct ISEORegDictionaryVtbl
    {
        BEGIN_INTERFACE
        
        HRESULT ( STDMETHODCALLTYPE *QueryInterface )( 
            ISEORegDictionary * This,
            /* [in] */ REFIID riid,
            /* [annotation][iid_is][out] */ 
            __RPC__deref_out  void **ppvObject);
        
        ULONG ( STDMETHODCALLTYPE *AddRef )( 
            ISEORegDictionary * This);
        
        ULONG ( STDMETHODCALLTYPE *Release )( 
            ISEORegDictionary * This);
        
        HRESULT ( STDMETHODCALLTYPE *GetTypeInfoCount )( 
            ISEORegDictionary * This,
            /* [out] */ UINT *pctinfo);
        
        HRESULT ( STDMETHODCALLTYPE *GetTypeInfo )( 
            ISEORegDictionary * This,
            /* [in] */ UINT iTInfo,
            /* [in] */ LCID lcid,
            /* [out] */ ITypeInfo **ppTInfo);
        
        HRESULT ( STDMETHODCALLTYPE *GetIDsOfNames )( 
            ISEORegDictionary * This,
            /* [in] */ REFIID riid,
            /* [size_is][in] */ LPOLESTR *rgszNames,
            /* [range][in] */ UINT cNames,
            /* [in] */ LCID lcid,
            /* [size_is][out] */ DISPID *rgDispId);
        
        /* [local] */ HRESULT ( STDMETHODCALLTYPE *Invoke )( 
            ISEORegDictionary * This,
            /* [in] */ DISPID dispIdMember,
            /* [in] */ REFIID riid,
            /* [in] */ LCID lcid,
            /* [in] */ WORD wFlags,
            /* [out][in] */ DISPPARAMS *pDispParams,
            /* [out] */ VARIANT *pVarResult,
            /* [out] */ EXCEPINFO *pExcepInfo,
            /* [out] */ UINT *puArgErr);
        
        /* [propget][id][helpstring] */ HRESULT ( STDMETHODCALLTYPE *get_Item )( 
            ISEORegDictionary * This,
            /* [in] */ VARIANT *pvarName,
            /* [retval][out] */ VARIANT *pvarResult);
        
        /* [propput][helpstring] */ HRESULT ( STDMETHODCALLTYPE *put_Item )( 
            ISEORegDictionary * This,
            /* [in] */ VARIANT *pvarName,
            /* [in] */ VARIANT *pvarValue);
        
        /* [hidden][propget][id][helpstring] */ HRESULT ( STDMETHODCALLTYPE *get__NewEnum )( 
            ISEORegDictionary * This,
            /* [retval][out] */ IUnknown **ppunkResult);
        
        /* [helpstring] */ HRESULT ( STDMETHODCALLTYPE *GetVariantA )( 
            ISEORegDictionary * This,
            /* [in] */ LPCSTR pszName,
            /* [retval][out] */ VARIANT *pvarResult);
        
        /* [helpstring] */ HRESULT ( STDMETHODCALLTYPE *GetVariantW )( 
            ISEORegDictionary * This,
            /* [in] */ LPCWSTR pszName,
            /* [retval][out] */ VARIANT *pvarResult);
        
        /* [helpstring] */ HRESULT ( STDMETHODCALLTYPE *SetVariantA )( 
            ISEORegDictionary * This,
            /* [in] */ LPCSTR pszName,
            /* [in] */ VARIANT *pvarValue);
        
        /* [helpstring] */ HRESULT ( STDMETHODCALLTYPE *SetVariantW )( 
            ISEORegDictionary * This,
            /* [in] */ LPCWSTR pszName,
            /* [in] */ VARIANT *pvarValue);
        
        /* [helpstring] */ HRESULT ( STDMETHODCALLTYPE *GetStringA )( 
            ISEORegDictionary * This,
            /* [in] */ LPCSTR pszName,
            /* [out][in] */ DWORD *pchCount,
            /* [size_is][out] */ LPSTR pszResult);
        
        /* [helpstring] */ HRESULT ( STDMETHODCALLTYPE *GetStringW )( 
            ISEORegDictionary * This,
            /* [in] */ LPCWSTR pszName,
            /* [out][in] */ DWORD *pchCount,
            /* [size_is][out] */ LPWSTR pszResult);
        
        /* [helpstring] */ HRESULT ( STDMETHODCALLTYPE *SetStringA )( 
            ISEORegDictionary * This,
            /* [in] */ LPCSTR pszName,
            /* [in] */ DWORD chCount,
            /* [size_is][in] */ LPCSTR pszValue);
        
        /* [helpstring] */ HRESULT ( STDMETHODCALLTYPE *SetStringW )( 
            ISEORegDictionary * This,
            /* [in] */ LPCWSTR pszName,
            /* [in] */ DWORD chCount,
            /* [size_is][in] */ LPCWSTR pszValue);
        
        /* [helpstring] */ HRESULT ( STDMETHODCALLTYPE *GetDWordA )( 
            ISEORegDictionary * This,
            /* [in] */ LPCSTR pszName,
            /* [retval][out] */ DWORD *pdwResult);
        
        /* [helpstring] */ HRESULT ( STDMETHODCALLTYPE *GetDWordW )( 
            ISEORegDictionary * This,
            /* [in] */ LPCWSTR pszName,
            /* [retval][out] */ DWORD *pdwResult);
        
        /* [helpstring] */ HRESULT ( STDMETHODCALLTYPE *SetDWordA )( 
            ISEORegDictionary * This,
            /* [in] */ LPCSTR pszName,
            /* [in] */ DWORD dwValue);
        
        /* [helpstring] */ HRESULT ( STDMETHODCALLTYPE *SetDWordW )( 
            ISEORegDictionary * This,
            /* [in] */ LPCWSTR pszName,
            /* [in] */ DWORD dwValue);
        
        /* [helpstring] */ HRESULT ( STDMETHODCALLTYPE *GetInterfaceA )( 
            ISEORegDictionary * This,
            /* [in] */ LPCSTR pszName,
            /* [in] */ REFIID iidDesired,
            /* [retval][iid_is][out] */ IUnknown **ppunkResult);
        
        /* [helpstring] */ HRESULT ( STDMETHODCALLTYPE *GetInterfaceW )( 
            ISEORegDictionary * This,
            /* [in] */ LPCWSTR pszName,
            /* [in] */ REFIID iidDesired,
            /* [retval][iid_is][out] */ IUnknown **ppunkResult);
        
        /* [helpstring] */ HRESULT ( STDMETHODCALLTYPE *SetInterfaceA )( 
            ISEORegDictionary * This,
            /* [in] */ LPCSTR pszName,
            /* [unique][in] */ IUnknown *punkValue);
        
        /* [helpstring] */ HRESULT ( STDMETHODCALLTYPE *SetInterfaceW )( 
            ISEORegDictionary * This,
            /* [in] */ LPCWSTR pszName,
            /* [unique][in] */ IUnknown *punkValue);
        
        /* [helpstring] */ HRESULT ( STDMETHODCALLTYPE *Load )( 
            ISEORegDictionary * This,
            /* [in] */ LPCOLESTR pszMachine,
            /* [in] */ SEO_HKEY skBaseKey,
            /* [in] */ LPCOLESTR pszSubKey,
            /* [unique][in] */ IErrorLog *pErrorLog);
        
        END_INTERFACE
    } ISEORegDictionaryVtbl;

    interface ISEORegDictionary
    {
        CONST_VTBL struct ISEORegDictionaryVtbl *lpVtbl;
    };

    

#ifdef COBJMACROS


#define ISEORegDictionary_QueryInterface(This,riid,ppvObject)	\
    ( (This)->lpVtbl -> QueryInterface(This,riid,ppvObject) ) 

#define ISEORegDictionary_AddRef(This)	\
    ( (This)->lpVtbl -> AddRef(This) ) 

#define ISEORegDictionary_Release(This)	\
    ( (This)->lpVtbl -> Release(This) ) 


#define ISEORegDictionary_GetTypeInfoCount(This,pctinfo)	\
    ( (This)->lpVtbl -> GetTypeInfoCount(This,pctinfo) ) 

#define ISEORegDictionary_GetTypeInfo(This,iTInfo,lcid,ppTInfo)	\
    ( (This)->lpVtbl -> GetTypeInfo(This,iTInfo,lcid,ppTInfo) ) 

#define ISEORegDictionary_GetIDsOfNames(This,riid,rgszNames,cNames,lcid,rgDispId)	\
    ( (This)->lpVtbl -> GetIDsOfNames(This,riid,rgszNames,cNames,lcid,rgDispId) ) 

#define ISEORegDictionary_Invoke(This,dispIdMember,riid,lcid,wFlags,pDispParams,pVarResult,pExcepInfo,puArgErr)	\
    ( (This)->lpVtbl -> Invoke(This,dispIdMember,riid,lcid,wFlags,pDispParams,pVarResult,pExcepInfo,puArgErr) ) 


#define ISEORegDictionary_get_Item(This,pvarName,pvarResult)	\
    ( (This)->lpVtbl -> get_Item(This,pvarName,pvarResult) ) 

#define ISEORegDictionary_put_Item(This,pvarName,pvarValue)	\
    ( (This)->lpVtbl -> put_Item(This,pvarName,pvarValue) ) 

#define ISEORegDictionary_get__NewEnum(This,ppunkResult)	\
    ( (This)->lpVtbl -> get__NewEnum(This,ppunkResult) ) 

#define ISEORegDictionary_GetVariantA(This,pszName,pvarResult)	\
    ( (This)->lpVtbl -> GetVariantA(This,pszName,pvarResult) ) 

#define ISEORegDictionary_GetVariantW(This,pszName,pvarResult)	\
    ( (This)->lpVtbl -> GetVariantW(This,pszName,pvarResult) ) 

#define ISEORegDictionary_SetVariantA(This,pszName,pvarValue)	\
    ( (This)->lpVtbl -> SetVariantA(This,pszName,pvarValue) ) 

#define ISEORegDictionary_SetVariantW(This,pszName,pvarValue)	\
    ( (This)->lpVtbl -> SetVariantW(This,pszName,pvarValue) ) 

#define ISEORegDictionary_GetStringA(This,pszName,pchCount,pszResult)	\
    ( (This)->lpVtbl -> GetStringA(This,pszName,pchCount,pszResult) ) 

#define ISEORegDictionary_GetStringW(This,pszName,pchCount,pszResult)	\
    ( (This)->lpVtbl -> GetStringW(This,pszName,pchCount,pszResult) ) 

#define ISEORegDictionary_SetStringA(This,pszName,chCount,pszValue)	\
    ( (This)->lpVtbl -> SetStringA(This,pszName,chCount,pszValue) ) 

#define ISEORegDictionary_SetStringW(This,pszName,chCount,pszValue)	\
    ( (This)->lpVtbl -> SetStringW(This,pszName,chCount,pszValue) ) 

#define ISEORegDictionary_GetDWordA(This,pszName,pdwResult)	\
    ( (This)->lpVtbl -> GetDWordA(This,pszName,pdwResult) ) 

#define ISEORegDictionary_GetDWordW(This,pszName,pdwResult)	\
    ( (This)->lpVtbl -> GetDWordW(This,pszName,pdwResult) ) 

#define ISEORegDictionary_SetDWordA(This,pszName,dwValue)	\
    ( (This)->lpVtbl -> SetDWordA(This,pszName,dwValue) ) 

#define ISEORegDictionary_SetDWordW(This,pszName,dwValue)	\
    ( (This)->lpVtbl -> SetDWordW(This,pszName,dwValue) ) 

#define ISEORegDictionary_GetInterfaceA(This,pszName,iidDesired,ppunkResult)	\
    ( (This)->lpVtbl -> GetInterfaceA(This,pszName,iidDesired,ppunkResult) ) 

#define ISEORegDictionary_GetInterfaceW(This,pszName,iidDesired,ppunkResult)	\
    ( (This)->lpVtbl -> GetInterfaceW(This,pszName,iidDesired,ppunkResult) ) 

#define ISEORegDictionary_SetInterfaceA(This,pszName,punkValue)	\
    ( (This)->lpVtbl -> SetInterfaceA(This,pszName,punkValue) ) 

#define ISEORegDictionary_SetInterfaceW(This,pszName,punkValue)	\
    ( (This)->lpVtbl -> SetInterfaceW(This,pszName,punkValue) ) 


#define ISEORegDictionary_Load(This,pszMachine,skBaseKey,pszSubKey,pErrorLog)	\
    ( (This)->lpVtbl -> Load(This,pszMachine,skBaseKey,pszSubKey,pErrorLog) ) 

#endif /* COBJMACROS */


#endif 	/* C style interface */




#endif 	/* __ISEORegDictionary_INTERFACE_DEFINED__ */


#ifndef __ISEOBindingConverter_INTERFACE_DEFINED__
#define __ISEOBindingConverter_INTERFACE_DEFINED__

/* interface ISEOBindingConverter */
/* [uuid][unique][object][helpstring] */ 


EXTERN_C const IID IID_ISEOBindingConverter;

#if defined(__cplusplus) && !defined(CINTERFACE)
    
    MIDL_INTERFACE("ee4e64d0-31f1-11d0-a9d0-00aa00685c74")
    ISEOBindingConverter : public IUnknown
    {
    public:
        virtual /* [helpstring] */ HRESULT STDMETHODCALLTYPE Convert( 
            /* [in] */ LONG lEventData,
            /* [retval][out] */ ISEODictionary **ppiResult) = 0;
        
    };
    
#else 	/* C style interface */

    typedef struct ISEOBindingConverterVtbl
    {
        BEGIN_INTERFACE
        
        HRESULT ( STDMETHODCALLTYPE *QueryInterface )( 
            ISEOBindingConverter * This,
            /* [in] */ REFIID riid,
            /* [annotation][iid_is][out] */ 
            __RPC__deref_out  void **ppvObject);
        
        ULONG ( STDMETHODCALLTYPE *AddRef )( 
            ISEOBindingConverter * This);
        
        ULONG ( STDMETHODCALLTYPE *Release )( 
            ISEOBindingConverter * This);
        
        /* [helpstring] */ HRESULT ( STDMETHODCALLTYPE *Convert )( 
            ISEOBindingConverter * This,
            /* [in] */ LONG lEventData,
            /* [retval][out] */ ISEODictionary **ppiResult);
        
        END_INTERFACE
    } ISEOBindingConverterVtbl;

    interface ISEOBindingConverter
    {
        CONST_VTBL struct ISEOBindingConverterVtbl *lpVtbl;
    };

    

#ifdef COBJMACROS


#define ISEOBindingConverter_QueryInterface(This,riid,ppvObject)	\
    ( (This)->lpVtbl -> QueryInterface(This,riid,ppvObject) ) 

#define ISEOBindingConverter_AddRef(This)	\
    ( (This)->lpVtbl -> AddRef(This) ) 

#define ISEOBindingConverter_Release(This)	\
    ( (This)->lpVtbl -> Release(This) ) 


#define ISEOBindingConverter_Convert(This,lEventData,ppiResult)	\
    ( (This)->lpVtbl -> Convert(This,lEventData,ppiResult) ) 

#endif /* COBJMACROS */


#endif 	/* C style interface */




#endif 	/* __ISEOBindingConverter_INTERFACE_DEFINED__ */


#ifndef __ISEODispatcher_INTERFACE_DEFINED__
#define __ISEODispatcher_INTERFACE_DEFINED__

/* interface ISEODispatcher */
/* [uuid][unique][object][hidden][helpstring] */ 


EXTERN_C const IID IID_ISEODispatcher;

#if defined(__cplusplus) && !defined(CINTERFACE)
    
    MIDL_INTERFACE("8ca89880-31f1-11d0-a9d0-00aa00685c74")
    ISEODispatcher : public IUnknown
    {
    public:
        virtual /* [helpstring] */ HRESULT STDMETHODCALLTYPE SetContext( 
            /* [unique][in] */ ISEORouter *piRouter,
            /* [unique][in] */ ISEODictionary *pdictBP) = 0;
        
    };
    
#else 	/* C style interface */

    typedef struct ISEODispatcherVtbl
    {
        BEGIN_INTERFACE
        
        HRESULT ( STDMETHODCALLTYPE *QueryInterface )( 
            ISEODispatcher * This,
            /* [in] */ REFIID riid,
            /* [annotation][iid_is][out] */ 
            __RPC__deref_out  void **ppvObject);
        
        ULONG ( STDMETHODCALLTYPE *AddRef )( 
            ISEODispatcher * This);
        
        ULONG ( STDMETHODCALLTYPE *Release )( 
            ISEODispatcher * This);
        
        /* [helpstring] */ HRESULT ( STDMETHODCALLTYPE *SetContext )( 
            ISEODispatcher * This,
            /* [unique][in] */ ISEORouter *piRouter,
            /* [unique][in] */ ISEODictionary *pdictBP);
        
        END_INTERFACE
    } ISEODispatcherVtbl;

    interface ISEODispatcher
    {
        CONST_VTBL struct ISEODispatcherVtbl *lpVtbl;
    };

    

#ifdef COBJMACROS


#define ISEODispatcher_QueryInterface(This,riid,ppvObject)	\
    ( (This)->lpVtbl -> QueryInterface(This,riid,ppvObject) ) 

#define ISEODispatcher_AddRef(This)	\
    ( (This)->lpVtbl -> AddRef(This) ) 

#define ISEODispatcher_Release(This)	\
    ( (This)->lpVtbl -> Release(This) ) 


#define ISEODispatcher_SetContext(This,piRouter,pdictBP)	\
    ( (This)->lpVtbl -> SetContext(This,piRouter,pdictBP) ) 

#endif /* COBJMACROS */


#endif 	/* C style interface */




#endif 	/* __ISEODispatcher_INTERFACE_DEFINED__ */


#ifndef __IEventDeliveryOptions_INTERFACE_DEFINED__
#define __IEventDeliveryOptions_INTERFACE_DEFINED__

/* interface IEventDeliveryOptions */
/* [uuid][unique][oleautomation][object][helpstring][dual] */ 


EXTERN_C const IID IID_IEventDeliveryOptions;

#if defined(__cplusplus) && !defined(CINTERFACE)
    
    MIDL_INTERFACE("0688a660-a3ff-11d0-a9e9-00aa00685c74")
    IEventDeliveryOptions : public IDispatch
    {
    public:
    };
    
#else 	/* C style interface */

    typedef struct IEventDeliveryOptionsVtbl
    {
        BEGIN_INTERFACE
        
        HRESULT ( STDMETHODCALLTYPE *QueryInterface )( 
            IEventDeliveryOptions * This,
            /* [in] */ REFIID riid,
            /* [annotation][iid_is][out] */ 
            __RPC__deref_out  void **ppvObject);
        
        ULONG ( STDMETHODCALLTYPE *AddRef )( 
            IEventDeliveryOptions * This);
        
        ULONG ( STDMETHODCALLTYPE *Release )( 
            IEventDeliveryOptions * This);
        
        HRESULT ( STDMETHODCALLTYPE *GetTypeInfoCount )( 
            IEventDeliveryOptions * This,
            /* [out] */ UINT *pctinfo);
        
        HRESULT ( STDMETHODCALLTYPE *GetTypeInfo )( 
            IEventDeliveryOptions * This,
            /* [in] */ UINT iTInfo,
            /* [in] */ LCID lcid,
            /* [out] */ ITypeInfo **ppTInfo);
        
        HRESULT ( STDMETHODCALLTYPE *GetIDsOfNames )( 
            IEventDeliveryOptions * This,
            /* [in] */ REFIID riid,
            /* [size_is][in] */ LPOLESTR *rgszNames,
            /* [range][in] */ UINT cNames,
            /* [in] */ LCID lcid,
            /* [size_is][out] */ DISPID *rgDispId);
        
        /* [local] */ HRESULT ( STDMETHODCALLTYPE *Invoke )( 
            IEventDeliveryOptions * This,
            /* [in] */ DISPID dispIdMember,
            /* [in] */ REFIID riid,
            /* [in] */ LCID lcid,
            /* [in] */ WORD wFlags,
            /* [out][in] */ DISPPARAMS *pDispParams,
            /* [out] */ VARIANT *pVarResult,
            /* [out] */ EXCEPINFO *pExcepInfo,
            /* [out] */ UINT *puArgErr);
        
        END_INTERFACE
    } IEventDeliveryOptionsVtbl;

    interface IEventDeliveryOptions
    {
        CONST_VTBL struct IEventDeliveryOptionsVtbl *lpVtbl;
    };

    

#ifdef COBJMACROS


#define IEventDeliveryOptions_QueryInterface(This,riid,ppvObject)	\
    ( (This)->lpVtbl -> QueryInterface(This,riid,ppvObject) ) 

#define IEventDeliveryOptions_AddRef(This)	\
    ( (This)->lpVtbl -> AddRef(This) ) 

#define IEventDeliveryOptions_Release(This)	\
    ( (This)->lpVtbl -> Release(This) ) 


#define IEventDeliveryOptions_GetTypeInfoCount(This,pctinfo)	\
    ( (This)->lpVtbl -> GetTypeInfoCount(This,pctinfo) ) 

#define IEventDeliveryOptions_GetTypeInfo(This,iTInfo,lcid,ppTInfo)	\
    ( (This)->lpVtbl -> GetTypeInfo(This,iTInfo,lcid,ppTInfo) ) 

#define IEventDeliveryOptions_GetIDsOfNames(This,riid,rgszNames,cNames,lcid,rgDispId)	\
    ( (This)->lpVtbl -> GetIDsOfNames(This,riid,rgszNames,cNames,lcid,rgDispId) ) 

#define IEventDeliveryOptions_Invoke(This,dispIdMember,riid,lcid,wFlags,pDispParams,pVarResult,pExcepInfo,puArgErr)	\
    ( (This)->lpVtbl -> Invoke(This,dispIdMember,riid,lcid,wFlags,pDispParams,pVarResult,pExcepInfo,puArgErr) ) 


#endif /* COBJMACROS */


#endif 	/* C style interface */




#endif 	/* __IEventDeliveryOptions_INTERFACE_DEFINED__ */


#ifndef __IEventTypeSinks_INTERFACE_DEFINED__
#define __IEventTypeSinks_INTERFACE_DEFINED__

/* interface IEventTypeSinks */
/* [uuid][unique][oleautomation][object][helpstring][dual] */ 


EXTERN_C const IID IID_IEventTypeSinks;

#if defined(__cplusplus) && !defined(CINTERFACE)
    
    MIDL_INTERFACE("a1063f50-a654-11d0-a9ea-00aa00685c74")
    IEventTypeSinks : public IDispatch
    {
    public:
        virtual /* [id][helpstring] */ HRESULT STDMETHODCALLTYPE Item( 
            /* [in] */ long lIndex,
            /* [retval][out] */ BSTR *pstrTypeSink) = 0;
        
        virtual /* [helpstring] */ HRESULT STDMETHODCALLTYPE Add( 
            /* [in] */ BSTR pszTypeSink) = 0;
        
        virtual /* [helpstring] */ HRESULT STDMETHODCALLTYPE Remove( 
            /* [in] */ BSTR pszTypeSink) = 0;
        
        virtual /* [propget][helpstring] */ HRESULT STDMETHODCALLTYPE get_Count( 
            /* [retval][out] */ long *plCount) = 0;
        
        virtual /* [hidden][propget][id][helpstring] */ HRESULT STDMETHODCALLTYPE get__NewEnum( 
            /* [retval][out] */ IUnknown **ppUnkEnum) = 0;
        
    };
    
#else 	/* C style interface */

    typedef struct IEventTypeSinksVtbl
    {
        BEGIN_INTERFACE
        
        HRESULT ( STDMETHODCALLTYPE *QueryInterface )( 
            IEventTypeSinks * This,
            /* [in] */ REFIID riid,
            /* [annotation][iid_is][out] */ 
            __RPC__deref_out  void **ppvObject);
        
        ULONG ( STDMETHODCALLTYPE *AddRef )( 
            IEventTypeSinks * This);
        
        ULONG ( STDMETHODCALLTYPE *Release )( 
            IEventTypeSinks * This);
        
        HRESULT ( STDMETHODCALLTYPE *GetTypeInfoCount )( 
            IEventTypeSinks * This,
            /* [out] */ UINT *pctinfo);
        
        HRESULT ( STDMETHODCALLTYPE *GetTypeInfo )( 
            IEventTypeSinks * This,
            /* [in] */ UINT iTInfo,
            /* [in] */ LCID lcid,
            /* [out] */ ITypeInfo **ppTInfo);
        
        HRESULT ( STDMETHODCALLTYPE *GetIDsOfNames )( 
            IEventTypeSinks * This,
            /* [in] */ REFIID riid,
            /* [size_is][in] */ LPOLESTR *rgszNames,
            /* [range][in] */ UINT cNames,
            /* [in] */ LCID lcid,
            /* [size_is][out] */ DISPID *rgDispId);
        
        /* [local] */ HRESULT ( STDMETHODCALLTYPE *Invoke )( 
            IEventTypeSinks * This,
            /* [in] */ DISPID dispIdMember,
            /* [in] */ REFIID riid,
            /* [in] */ LCID lcid,
            /* [in] */ WORD wFlags,
            /* [out][in] */ DISPPARAMS *pDispParams,
            /* [out] */ VARIANT *pVarResult,
            /* [out] */ EXCEPINFO *pExcepInfo,
            /* [out] */ UINT *puArgErr);
        
        /* [id][helpstring] */ HRESULT ( STDMETHODCALLTYPE *Item )( 
            IEventTypeSinks * This,
            /* [in] */ long lIndex,
            /* [retval][out] */ BSTR *pstrTypeSink);
        
        /* [helpstring] */ HRESULT ( STDMETHODCALLTYPE *Add )( 
            IEventTypeSinks * This,
            /* [in] */ BSTR pszTypeSink);
        
        /* [helpstring] */ HRESULT ( STDMETHODCALLTYPE *Remove )( 
            IEventTypeSinks * This,
            /* [in] */ BSTR pszTypeSink);
        
        /* [propget][helpstring] */ HRESULT ( STDMETHODCALLTYPE *get_Count )( 
            IEventTypeSinks * This,
            /* [retval][out] */ long *plCount);
        
        /* [hidden][propget][id][helpstring] */ HRESULT ( STDMETHODCALLTYPE *get__NewEnum )( 
            IEventTypeSinks * This,
            /* [retval][out] */ IUnknown **ppUnkEnum);
        
        END_INTERFACE
    } IEventTypeSinksVtbl;

    interface IEventTypeSinks
    {
        CONST_VTBL struct IEventTypeSinksVtbl *lpVtbl;
    };

    

#ifdef COBJMACROS


#define IEventTypeSinks_QueryInterface(This,riid,ppvObject)	\
    ( (This)->lpVtbl -> QueryInterface(This,riid,ppvObject) ) 

#define IEventTypeSinks_AddRef(This)	\
    ( (This)->lpVtbl -> AddRef(This) ) 

#define IEventTypeSinks_Release(This)	\
    ( (This)->lpVtbl -> Release(This) ) 


#define IEventTypeSinks_GetTypeInfoCount(This,pctinfo)	\
    ( (This)->lpVtbl -> GetTypeInfoCount(This,pctinfo) ) 

#define IEventTypeSinks_GetTypeInfo(This,iTInfo,lcid,ppTInfo)	\
    ( (This)->lpVtbl -> GetTypeInfo(This,iTInfo,lcid,ppTInfo) ) 

#define IEventTypeSinks_GetIDsOfNames(This,riid,rgszNames,cNames,lcid,rgDispId)	\
    ( (This)->lpVtbl -> GetIDsOfNames(This,riid,rgszNames,cNames,lcid,rgDispId) ) 

#define IEventTypeSinks_Invoke(This,dispIdMember,riid,lcid,wFlags,pDispParams,pVarResult,pExcepInfo,puArgErr)	\
    ( (This)->lpVtbl -> Invoke(This,dispIdMember,riid,lcid,wFlags,pDispParams,pVarResult,pExcepInfo,puArgErr) ) 


#define IEventTypeSinks_Item(This,lIndex,pstrTypeSink)	\
    ( (This)->lpVtbl -> Item(This,lIndex,pstrTypeSink) ) 

#define IEventTypeSinks_Add(This,pszTypeSink)	\
    ( (This)->lpVtbl -> Add(This,pszTypeSink) ) 

#define IEventTypeSinks_Remove(This,pszTypeSink)	\
    ( (This)->lpVtbl -> Remove(This,pszTypeSink) ) 

#define IEventTypeSinks_get_Count(This,plCount)	\
    ( (This)->lpVtbl -> get_Count(This,plCount) ) 

#define IEventTypeSinks_get__NewEnum(This,ppUnkEnum)	\
    ( (This)->lpVtbl -> get__NewEnum(This,ppUnkEnum) ) 

#endif /* COBJMACROS */


#endif 	/* C style interface */




#endif 	/* __IEventTypeSinks_INTERFACE_DEFINED__ */


#ifndef __IEventType_INTERFACE_DEFINED__
#define __IEventType_INTERFACE_DEFINED__

/* interface IEventType */
/* [uuid][unique][oleautomation][object][helpstring][dual] */ 


EXTERN_C const IID IID_IEventType;

#if defined(__cplusplus) && !defined(CINTERFACE)
    
    MIDL_INTERFACE("4a993b80-a654-11d0-a9ea-00aa00685c74")
    IEventType : public IDispatch
    {
    public:
        virtual /* [propget][helpstring] */ HRESULT STDMETHODCALLTYPE get_ID( 
            /* [retval][out] */ BSTR *pstrID) = 0;
        
        virtual /* [propget][helpstring] */ HRESULT STDMETHODCALLTYPE get_DisplayName( 
            /* [retval][out] */ BSTR *pstrDisplayName) = 0;
        
        virtual /* [propget][helpstring] */ HRESULT STDMETHODCALLTYPE get_Sinks( 
            /* [retval][out] */ IEventTypeSinks **ppTypeSinks) = 0;
        
    };
    
#else 	/* C style interface */

    typedef struct IEventTypeVtbl
    {
        BEGIN_INTERFACE
        
        HRESULT ( STDMETHODCALLTYPE *QueryInterface )( 
            IEventType * This,
            /* [in] */ REFIID riid,
            /* [annotation][iid_is][out] */ 
            __RPC__deref_out  void **ppvObject);
        
        ULONG ( STDMETHODCALLTYPE *AddRef )( 
            IEventType * This);
        
        ULONG ( STDMETHODCALLTYPE *Release )( 
            IEventType * This);
        
        HRESULT ( STDMETHODCALLTYPE *GetTypeInfoCount )( 
            IEventType * This,
            /* [out] */ UINT *pctinfo);
        
        HRESULT ( STDMETHODCALLTYPE *GetTypeInfo )( 
            IEventType * This,
            /* [in] */ UINT iTInfo,
            /* [in] */ LCID lcid,
            /* [out] */ ITypeInfo **ppTInfo);
        
        HRESULT ( STDMETHODCALLTYPE *GetIDsOfNames )( 
            IEventType * This,
            /* [in] */ REFIID riid,
            /* [size_is][in] */ LPOLESTR *rgszNames,
            /* [range][in] */ UINT cNames,
            /* [in] */ LCID lcid,
            /* [size_is][out] */ DISPID *rgDispId);
        
        /* [local] */ HRESULT ( STDMETHODCALLTYPE *Invoke )( 
            IEventType * This,
            /* [in] */ DISPID dispIdMember,
            /* [in] */ REFIID riid,
            /* [in] */ LCID lcid,
            /* [in] */ WORD wFlags,
            /* [out][in] */ DISPPARAMS *pDispParams,
            /* [out] */ VARIANT *pVarResult,
            /* [out] */ EXCEPINFO *pExcepInfo,
            /* [out] */ UINT *puArgErr);
        
        /* [propget][helpstring] */ HRESULT ( STDMETHODCALLTYPE *get_ID )( 
            IEventType * This,
            /* [retval][out] */ BSTR *pstrID);
        
        /* [propget][helpstring] */ HRESULT ( STDMETHODCALLTYPE *get_DisplayName )( 
            IEventType * This,
            /* [retval][out] */ BSTR *pstrDisplayName);
        
        /* [propget][helpstring] */ HRESULT ( STDMETHODCALLTYPE *get_Sinks )( 
            IEventType * This,
            /* [retval][out] */ IEventTypeSinks **ppTypeSinks);
        
        END_INTERFACE
    } IEventTypeVtbl;

    interface IEventType
    {
        CONST_VTBL struct IEventTypeVtbl *lpVtbl;
    };

    

#ifdef COBJMACROS


#define IEventType_QueryInterface(This,riid,ppvObject)	\
    ( (This)->lpVtbl -> QueryInterface(This,riid,ppvObject) ) 

#define IEventType_AddRef(This)	\
    ( (This)->lpVtbl -> AddRef(This) ) 

#define IEventType_Release(This)	\
    ( (This)->lpVtbl -> Release(This) ) 


#define IEventType_GetTypeInfoCount(This,pctinfo)	\
    ( (This)->lpVtbl -> GetTypeInfoCount(This,pctinfo) ) 

#define IEventType_GetTypeInfo(This,iTInfo,lcid,ppTInfo)	\
    ( (This)->lpVtbl -> GetTypeInfo(This,iTInfo,lcid,ppTInfo) ) 

#define IEventType_GetIDsOfNames(This,riid,rgszNames,cNames,lcid,rgDispId)	\
    ( (This)->lpVtbl -> GetIDsOfNames(This,riid,rgszNames,cNames,lcid,rgDispId) ) 

#define IEventType_Invoke(This,dispIdMember,riid,lcid,wFlags,pDispParams,pVarResult,pExcepInfo,puArgErr)	\
    ( (This)->lpVtbl -> Invoke(This,dispIdMember,riid,lcid,wFlags,pDispParams,pVarResult,pExcepInfo,puArgErr) ) 


#define IEventType_get_ID(This,pstrID)	\
    ( (This)->lpVtbl -> get_ID(This,pstrID) ) 

#define IEventType_get_DisplayName(This,pstrDisplayName)	\
    ( (This)->lpVtbl -> get_DisplayName(This,pstrDisplayName) ) 

#define IEventType_get_Sinks(This,ppTypeSinks)	\
    ( (This)->lpVtbl -> get_Sinks(This,ppTypeSinks) ) 

#endif /* COBJMACROS */


#endif 	/* C style interface */




#endif 	/* __IEventType_INTERFACE_DEFINED__ */


#ifndef __IEventPropertyBag_INTERFACE_DEFINED__
#define __IEventPropertyBag_INTERFACE_DEFINED__

/* interface IEventPropertyBag */
/* [uuid][unique][oleautomation][object][helpstring][dual] */ 


EXTERN_C const IID IID_IEventPropertyBag;

#if defined(__cplusplus) && !defined(CINTERFACE)
    
    MIDL_INTERFACE("aabb23e0-a705-11d0-a9ea-00aa00685c74")
    IEventPropertyBag : public IDispatch
    {
    public:
        virtual /* [id][helpstring] */ HRESULT STDMETHODCALLTYPE Item( 
            /* [in] */ VARIANT *pvarPropDesired,
            /* [retval][out] */ VARIANT *pvarPropValue) = 0;
        
        virtual /* [helpstring] */ HRESULT STDMETHODCALLTYPE Name( 
            /* [in] */ long lPropIndex,
            /* [retval][out] */ BSTR *pstrPropName) = 0;
        
        virtual /* [helpstring] */ HRESULT STDMETHODCALLTYPE Add( 
            /* [in] */ BSTR pszPropName,
            /* [in] */ VARIANT *pvarPropValue) = 0;
        
        virtual /* [helpstring] */ HRESULT STDMETHODCALLTYPE Remove( 
            /* [in] */ VARIANT *pvarPropDesired) = 0;
        
        virtual /* [propget][helpstring] */ HRESULT STDMETHODCALLTYPE get_Count( 
            /* [retval][out] */ long *plCount) = 0;
        
        virtual /* [hidden][propget][id][helpstring] */ HRESULT STDMETHODCALLTYPE get__NewEnum( 
            /* [retval][out] */ IUnknown **ppUnkEnum) = 0;
        
    };
    
#else 	/* C style interface */

    typedef struct IEventPropertyBagVtbl
    {
        BEGIN_INTERFACE
        
        HRESULT ( STDMETHODCALLTYPE *QueryInterface )( 
            IEventPropertyBag * This,
            /* [in] */ REFIID riid,
            /* [annotation][iid_is][out] */ 
            __RPC__deref_out  void **ppvObject);
        
        ULONG ( STDMETHODCALLTYPE *AddRef )( 
            IEventPropertyBag * This);
        
        ULONG ( STDMETHODCALLTYPE *Release )( 
            IEventPropertyBag * This);
        
        HRESULT ( STDMETHODCALLTYPE *GetTypeInfoCount )( 
            IEventPropertyBag * This,
            /* [out] */ UINT *pctinfo);
        
        HRESULT ( STDMETHODCALLTYPE *GetTypeInfo )( 
            IEventPropertyBag * This,
            /* [in] */ UINT iTInfo,
            /* [in] */ LCID lcid,
            /* [out] */ ITypeInfo **ppTInfo);
        
        HRESULT ( STDMETHODCALLTYPE *GetIDsOfNames )( 
            IEventPropertyBag * This,
            /* [in] */ REFIID riid,
            /* [size_is][in] */ LPOLESTR *rgszNames,
            /* [range][in] */ UINT cNames,
            /* [in] */ LCID lcid,
            /* [size_is][out] */ DISPID *rgDispId);
        
        /* [local] */ HRESULT ( STDMETHODCALLTYPE *Invoke )( 
            IEventPropertyBag * This,
            /* [in] */ DISPID dispIdMember,
            /* [in] */ REFIID riid,
            /* [in] */ LCID lcid,
            /* [in] */ WORD wFlags,
            /* [out][in] */ DISPPARAMS *pDispParams,
            /* [out] */ VARIANT *pVarResult,
            /* [out] */ EXCEPINFO *pExcepInfo,
            /* [out] */ UINT *puArgErr);
        
        /* [id][helpstring] */ HRESULT ( STDMETHODCALLTYPE *Item )( 
            IEventPropertyBag * This,
            /* [in] */ VARIANT *pvarPropDesired,
            /* [retval][out] */ VARIANT *pvarPropValue);
        
        /* [helpstring] */ HRESULT ( STDMETHODCALLTYPE *Name )( 
            IEventPropertyBag * This,
            /* [in] */ long lPropIndex,
            /* [retval][out] */ BSTR *pstrPropName);
        
        /* [helpstring] */ HRESULT ( STDMETHODCALLTYPE *Add )( 
            IEventPropertyBag * This,
            /* [in] */ BSTR pszPropName,
            /* [in] */ VARIANT *pvarPropValue);
        
        /* [helpstring] */ HRESULT ( STDMETHODCALLTYPE *Remove )( 
            IEventPropertyBag * This,
            /* [in] */ VARIANT *pvarPropDesired);
        
        /* [propget][helpstring] */ HRESULT ( STDMETHODCALLTYPE *get_Count )( 
            IEventPropertyBag * This,
            /* [retval][out] */ long *plCount);
        
        /* [hidden][propget][id][helpstring] */ HRESULT ( STDMETHODCALLTYPE *get__NewEnum )( 
            IEventPropertyBag * This,
            /* [retval][out] */ IUnknown **ppUnkEnum);
        
        END_INTERFACE
    } IEventPropertyBagVtbl;

    interface IEventPropertyBag
    {
        CONST_VTBL struct IEventPropertyBagVtbl *lpVtbl;
    };

    

#ifdef COBJMACROS


#define IEventPropertyBag_QueryInterface(This,riid,ppvObject)	\
    ( (This)->lpVtbl -> QueryInterface(This,riid,ppvObject) ) 

#define IEventPropertyBag_AddRef(This)	\
    ( (This)->lpVtbl -> AddRef(This) ) 

#define IEventPropertyBag_Release(This)	\
    ( (This)->lpVtbl -> Release(This) ) 


#define IEventPropertyBag_GetTypeInfoCount(This,pctinfo)	\
    ( (This)->lpVtbl -> GetTypeInfoCount(This,pctinfo) ) 

#define IEventPropertyBag_GetTypeInfo(This,iTInfo,lcid,ppTInfo)	\
    ( (This)->lpVtbl -> GetTypeInfo(This,iTInfo,lcid,ppTInfo) ) 

#define IEventPropertyBag_GetIDsOfNames(This,riid,rgszNames,cNames,lcid,rgDispId)	\
    ( (This)->lpVtbl -> GetIDsOfNames(This,riid,rgszNames,cNames,lcid,rgDispId) ) 

#define IEventPropertyBag_Invoke(This,dispIdMember,riid,lcid,wFlags,pDispParams,pVarResult,pExcepInfo,puArgErr)	\
    ( (This)->lpVtbl -> Invoke(This,dispIdMember,riid,lcid,wFlags,pDispParams,pVarResult,pExcepInfo,puArgErr) ) 


#define IEventPropertyBag_Item(This,pvarPropDesired,pvarPropValue)	\
    ( (This)->lpVtbl -> Item(This,pvarPropDesired,pvarPropValue) ) 

#define IEventPropertyBag_Name(This,lPropIndex,pstrPropName)	\
    ( (This)->lpVtbl -> Name(This,lPropIndex,pstrPropName) ) 

#define IEventPropertyBag_Add(This,pszPropName,pvarPropValue)	\
    ( (This)->lpVtbl -> Add(This,pszPropName,pvarPropValue) ) 

#define IEventPropertyBag_Remove(This,pvarPropDesired)	\
    ( (This)->lpVtbl -> Remove(This,pvarPropDesired) ) 

#define IEventPropertyBag_get_Count(This,plCount)	\
    ( (This)->lpVtbl -> get_Count(This,plCount) ) 

#define IEventPropertyBag_get__NewEnum(This,ppUnkEnum)	\
    ( (This)->lpVtbl -> get__NewEnum(This,ppUnkEnum) ) 

#endif /* COBJMACROS */


#endif 	/* C style interface */




#endif 	/* __IEventPropertyBag_INTERFACE_DEFINED__ */


#ifndef __IEventBinding_INTERFACE_DEFINED__
#define __IEventBinding_INTERFACE_DEFINED__

/* interface IEventBinding */
/* [uuid][unique][oleautomation][object][helpstring][dual] */ 


EXTERN_C const IID IID_IEventBinding;

#if defined(__cplusplus) && !defined(CINTERFACE)
    
    MIDL_INTERFACE("8e398ce0-a64e-11d0-a9ea-00aa00685c74")
    IEventBinding : public IDispatch
    {
    public:
        virtual /* [propget][helpstring] */ HRESULT STDMETHODCALLTYPE get_ID( 
            /* [retval][out] */ BSTR *pstrBindingID) = 0;
        
        virtual /* [propget][helpstring] */ HRESULT STDMETHODCALLTYPE get_DisplayName( 
            /* [retval][out] */ BSTR *pstrDisplayName) = 0;
        
        virtual /* [propput][helpstring] */ HRESULT STDMETHODCALLTYPE put_DisplayName( 
            /* [in] */ BSTR pszDisplayName) = 0;
        
        virtual /* [propputref][helpstring] */ HRESULT STDMETHODCALLTYPE putref_DisplayName( 
            /* [in] */ BSTR *ppszDisplayName) = 0;
        
        virtual /* [propget][helpstring] */ HRESULT STDMETHODCALLTYPE get_SinkClass( 
            /* [retval][out] */ BSTR *pstrSinkClass) = 0;
        
        virtual /* [propput][helpstring] */ HRESULT STDMETHODCALLTYPE put_SinkClass( 
            /* [in] */ BSTR pszSinkClass) = 0;
        
        virtual /* [propputref][helpstring] */ HRESULT STDMETHODCALLTYPE putref_SinkClass( 
            /* [in] */ BSTR *ppszSinkClass) = 0;
        
        virtual /* [propget][helpstring] */ HRESULT STDMETHODCALLTYPE get_SinkProperties( 
            /* [retval][out] */ IEventPropertyBag **ppSinkProperties) = 0;
        
        virtual /* [propget][helpstring] */ HRESULT STDMETHODCALLTYPE get_SourceProperties( 
            /* [retval][out] */ IEventPropertyBag **ppSourceProperties) = 0;
        
        virtual /* [propget][helpstring] */ HRESULT STDMETHODCALLTYPE get_EventBindingProperties( 
            /* [retval][out] */ IEventPropertyBag **ppEventBindingProperties) = 0;
        
        virtual /* [propget][helpstring] */ HRESULT STDMETHODCALLTYPE get_Enabled( 
            /* [retval][out] */ VARIANT_BOOL *pbEnabled) = 0;
        
        virtual /* [propput][helpstring] */ HRESULT STDMETHODCALLTYPE put_Enabled( 
            /* [in] */ VARIANT_BOOL bEnabled) = 0;
        
        virtual /* [propputref][helpstring] */ HRESULT STDMETHODCALLTYPE putref_Enabled( 
            /* [in] */ VARIANT_BOOL *pbEnabled) = 0;
        
        virtual /* [propget][helpstring] */ HRESULT STDMETHODCALLTYPE get_Expiration( 
            /* [retval][out] */ DATE *pdateExpiration) = 0;
        
        virtual /* [propput][helpstring] */ HRESULT STDMETHODCALLTYPE put_Expiration( 
            /* [in] */ DATE dateExpiration) = 0;
        
        virtual /* [propputref][helpstring] */ HRESULT STDMETHODCALLTYPE putref_Expiration( 
            /* [in] */ DATE *pdateExpiration) = 0;
        
        virtual /* [propget][helpstring] */ HRESULT STDMETHODCALLTYPE get_MaxFirings( 
            /* [retval][out] */ long *plMaxFirings) = 0;
        
        virtual /* [propput][helpstring] */ HRESULT STDMETHODCALLTYPE put_MaxFirings( 
            /* [in] */ long lMaxFirings) = 0;
        
        virtual /* [propputref][helpstring] */ HRESULT STDMETHODCALLTYPE putref_MaxFirings( 
            /* [in] */ long *plMaxFirings) = 0;
        
        virtual /* [helpstring] */ HRESULT STDMETHODCALLTYPE Save( void) = 0;
        
    };
    
#else 	/* C style interface */

    typedef struct IEventBindingVtbl
    {
        BEGIN_INTERFACE
        
        HRESULT ( STDMETHODCALLTYPE *QueryInterface )( 
            IEventBinding * This,
            /* [in] */ REFIID riid,
            /* [annotation][iid_is][out] */ 
            __RPC__deref_out  void **ppvObject);
        
        ULONG ( STDMETHODCALLTYPE *AddRef )( 
            IEventBinding * This);
        
        ULONG ( STDMETHODCALLTYPE *Release )( 
            IEventBinding * This);
        
        HRESULT ( STDMETHODCALLTYPE *GetTypeInfoCount )( 
            IEventBinding * This,
            /* [out] */ UINT *pctinfo);
        
        HRESULT ( STDMETHODCALLTYPE *GetTypeInfo )( 
            IEventBinding * This,
            /* [in] */ UINT iTInfo,
            /* [in] */ LCID lcid,
            /* [out] */ ITypeInfo **ppTInfo);
        
        HRESULT ( STDMETHODCALLTYPE *GetIDsOfNames )( 
            IEventBinding * This,
            /* [in] */ REFIID riid,
            /* [size_is][in] */ LPOLESTR *rgszNames,
            /* [range][in] */ UINT cNames,
            /* [in] */ LCID lcid,
            /* [size_is][out] */ DISPID *rgDispId);
        
        /* [local] */ HRESULT ( STDMETHODCALLTYPE *Invoke )( 
            IEventBinding * This,
            /* [in] */ DISPID dispIdMember,
            /* [in] */ REFIID riid,
            /* [in] */ LCID lcid,
            /* [in] */ WORD wFlags,
            /* [out][in] */ DISPPARAMS *pDispParams,
            /* [out] */ VARIANT *pVarResult,
            /* [out] */ EXCEPINFO *pExcepInfo,
            /* [out] */ UINT *puArgErr);
        
        /* [propget][helpstring] */ HRESULT ( STDMETHODCALLTYPE *get_ID )( 
            IEventBinding * This,
            /* [retval][out] */ BSTR *pstrBindingID);
        
        /* [propget][helpstring] */ HRESULT ( STDMETHODCALLTYPE *get_DisplayName )( 
            IEventBinding * This,
            /* [retval][out] */ BSTR *pstrDisplayName);
        
        /* [propput][helpstring] */ HRESULT ( STDMETHODCALLTYPE *put_DisplayName )( 
            IEventBinding * This,
            /* [in] */ BSTR pszDisplayName);
        
        /* [propputref][helpstring] */ HRESULT ( STDMETHODCALLTYPE *putref_DisplayName )( 
            IEventBinding * This,
            /* [in] */ BSTR *ppszDisplayName);
        
        /* [propget][helpstring] */ HRESULT ( STDMETHODCALLTYPE *get_SinkClass )( 
            IEventBinding * This,
            /* [retval][out] */ BSTR *pstrSinkClass);
        
        /* [propput][helpstring] */ HRESULT ( STDMETHODCALLTYPE *put_SinkClass )( 
            IEventBinding * This,
            /* [in] */ BSTR pszSinkClass);
        
        /* [propputref][helpstring] */ HRESULT ( STDMETHODCALLTYPE *putref_SinkClass )( 
            IEventBinding * This,
            /* [in] */ BSTR *ppszSinkClass);
        
        /* [propget][helpstring] */ HRESULT ( STDMETHODCALLTYPE *get_SinkProperties )( 
            IEventBinding * This,
            /* [retval][out] */ IEventPropertyBag **ppSinkProperties);
        
        /* [propget][helpstring] */ HRESULT ( STDMETHODCALLTYPE *get_SourceProperties )( 
            IEventBinding * This,
            /* [retval][out] */ IEventPropertyBag **ppSourceProperties);
        
        /* [propget][helpstring] */ HRESULT ( STDMETHODCALLTYPE *get_EventBindingProperties )( 
            IEventBinding * This,
            /* [retval][out] */ IEventPropertyBag **ppEventBindingProperties);
        
        /* [propget][helpstring] */ HRESULT ( STDMETHODCALLTYPE *get_Enabled )( 
            IEventBinding * This,
            /* [retval][out] */ VARIANT_BOOL *pbEnabled);
        
        /* [propput][helpstring] */ HRESULT ( STDMETHODCALLTYPE *put_Enabled )( 
            IEventBinding * This,
            /* [in] */ VARIANT_BOOL bEnabled);
        
        /* [propputref][helpstring] */ HRESULT ( STDMETHODCALLTYPE *putref_Enabled )( 
            IEventBinding * This,
            /* [in] */ VARIANT_BOOL *pbEnabled);
        
        /* [propget][helpstring] */ HRESULT ( STDMETHODCALLTYPE *get_Expiration )( 
            IEventBinding * This,
            /* [retval][out] */ DATE *pdateExpiration);
        
        /* [propput][helpstring] */ HRESULT ( STDMETHODCALLTYPE *put_Expiration )( 
            IEventBinding * This,
            /* [in] */ DATE dateExpiration);
        
        /* [propputref][helpstring] */ HRESULT ( STDMETHODCALLTYPE *putref_Expiration )( 
            IEventBinding * This,
            /* [in] */ DATE *pdateExpiration);
        
        /* [propget][helpstring] */ HRESULT ( STDMETHODCALLTYPE *get_MaxFirings )( 
            IEventBinding * This,
            /* [retval][out] */ long *plMaxFirings);
        
        /* [propput][helpstring] */ HRESULT ( STDMETHODCALLTYPE *put_MaxFirings )( 
            IEventBinding * This,
            /* [in] */ long lMaxFirings);
        
        /* [propputref][helpstring] */ HRESULT ( STDMETHODCALLTYPE *putref_MaxFirings )( 
            IEventBinding * This,
            /* [in] */ long *plMaxFirings);
        
        /* [helpstring] */ HRESULT ( STDMETHODCALLTYPE *Save )( 
            IEventBinding * This);
        
        END_INTERFACE
    } IEventBindingVtbl;

    interface IEventBinding
    {
        CONST_VTBL struct IEventBindingVtbl *lpVtbl;
    };

    

#ifdef COBJMACROS


#define IEventBinding_QueryInterface(This,riid,ppvObject)	\
    ( (This)->lpVtbl -> QueryInterface(This,riid,ppvObject) ) 

#define IEventBinding_AddRef(This)	\
    ( (This)->lpVtbl -> AddRef(This) ) 

#define IEventBinding_Release(This)	\
    ( (This)->lpVtbl -> Release(This) ) 


#define IEventBinding_GetTypeInfoCount(This,pctinfo)	\
    ( (This)->lpVtbl -> GetTypeInfoCount(This,pctinfo) ) 

#define IEventBinding_GetTypeInfo(This,iTInfo,lcid,ppTInfo)	\
    ( (This)->lpVtbl -> GetTypeInfo(This,iTInfo,lcid,ppTInfo) ) 

#define IEventBinding_GetIDsOfNames(This,riid,rgszNames,cNames,lcid,rgDispId)	\
    ( (This)->lpVtbl -> GetIDsOfNames(This,riid,rgszNames,cNames,lcid,rgDispId) ) 

#define IEventBinding_Invoke(This,dispIdMember,riid,lcid,wFlags,pDispParams,pVarResult,pExcepInfo,puArgErr)	\
    ( (This)->lpVtbl -> Invoke(This,dispIdMember,riid,lcid,wFlags,pDispParams,pVarResult,pExcepInfo,puArgErr) ) 


#define IEventBinding_get_ID(This,pstrBindingID)	\
    ( (This)->lpVtbl -> get_ID(This,pstrBindingID) ) 

#define IEventBinding_get_DisplayName(This,pstrDisplayName)	\
    ( (This)->lpVtbl -> get_DisplayName(This,pstrDisplayName) ) 

#define IEventBinding_put_DisplayName(This,pszDisplayName)	\
    ( (This)->lpVtbl -> put_DisplayName(This,pszDisplayName) ) 

#define IEventBinding_putref_DisplayName(This,ppszDisplayName)	\
    ( (This)->lpVtbl -> putref_DisplayName(This,ppszDisplayName) ) 

#define IEventBinding_get_SinkClass(This,pstrSinkClass)	\
    ( (This)->lpVtbl -> get_SinkClass(This,pstrSinkClass) ) 

#define IEventBinding_put_SinkClass(This,pszSinkClass)	\
    ( (This)->lpVtbl -> put_SinkClass(This,pszSinkClass) ) 

#define IEventBinding_putref_SinkClass(This,ppszSinkClass)	\
    ( (This)->lpVtbl -> putref_SinkClass(This,ppszSinkClass) ) 

#define IEventBinding_get_SinkProperties(This,ppSinkProperties)	\
    ( (This)->lpVtbl -> get_SinkProperties(This,ppSinkProperties) ) 

#define IEventBinding_get_SourceProperties(This,ppSourceProperties)	\
    ( (This)->lpVtbl -> get_SourceProperties(This,ppSourceProperties) ) 

#define IEventBinding_get_EventBindingProperties(This,ppEventBindingProperties)	\
    ( (This)->lpVtbl -> get_EventBindingProperties(This,ppEventBindingProperties) ) 

#define IEventBinding_get_Enabled(This,pbEnabled)	\
    ( (This)->lpVtbl -> get_Enabled(This,pbEnabled) ) 

#define IEventBinding_put_Enabled(This,bEnabled)	\
    ( (This)->lpVtbl -> put_Enabled(This,bEnabled) ) 

#define IEventBinding_putref_Enabled(This,pbEnabled)	\
    ( (This)->lpVtbl -> putref_Enabled(This,pbEnabled) ) 

#define IEventBinding_get_Expiration(This,pdateExpiration)	\
    ( (This)->lpVtbl -> get_Expiration(This,pdateExpiration) ) 

#define IEventBinding_put_Expiration(This,dateExpiration)	\
    ( (This)->lpVtbl -> put_Expiration(This,dateExpiration) ) 

#define IEventBinding_putref_Expiration(This,pdateExpiration)	\
    ( (This)->lpVtbl -> putref_Expiration(This,pdateExpiration) ) 

#define IEventBinding_get_MaxFirings(This,plMaxFirings)	\
    ( (This)->lpVtbl -> get_MaxFirings(This,plMaxFirings) ) 

#define IEventBinding_put_MaxFirings(This,lMaxFirings)	\
    ( (This)->lpVtbl -> put_MaxFirings(This,lMaxFirings) ) 

#define IEventBinding_putref_MaxFirings(This,plMaxFirings)	\
    ( (This)->lpVtbl -> putref_MaxFirings(This,plMaxFirings) ) 

#define IEventBinding_Save(This)	\
    ( (This)->lpVtbl -> Save(This) ) 

#endif /* COBJMACROS */


#endif 	/* C style interface */




#endif 	/* __IEventBinding_INTERFACE_DEFINED__ */


#ifndef __IEventBindings_INTERFACE_DEFINED__
#define __IEventBindings_INTERFACE_DEFINED__

/* interface IEventBindings */
/* [uuid][unique][oleautomation][object][helpstring][dual] */ 


EXTERN_C const IID IID_IEventBindings;

#if defined(__cplusplus) && !defined(CINTERFACE)
    
    MIDL_INTERFACE("1080b910-a636-11d0-a9ea-00aa00685c74")
    IEventBindings : public IDispatch
    {
    public:
        virtual /* [id][helpstring] */ HRESULT STDMETHODCALLTYPE Item( 
            /* [in] */ VARIANT *pvarDesired,
            /* [retval][out] */ IEventBinding **ppEventBinding) = 0;
        
        virtual /* [helpstring] */ HRESULT STDMETHODCALLTYPE Add( 
            /* [in] */ BSTR strBinding,
            /* [retval][out] */ IEventBinding **ppBinding) = 0;
        
        virtual /* [helpstring] */ HRESULT STDMETHODCALLTYPE Remove( 
            /* [in] */ VARIANT *pvarDesired) = 0;
        
        virtual /* [propget][helpstring] */ HRESULT STDMETHODCALLTYPE get_Count( 
            /* [retval][out] */ long *plCount) = 0;
        
        virtual /* [hidden][propget][id][helpstring] */ HRESULT STDMETHODCALLTYPE get__NewEnum( 
            /* [retval][out] */ IUnknown **ppUnkEnum) = 0;
        
    };
    
#else 	/* C style interface */

    typedef struct IEventBindingsVtbl
    {
        BEGIN_INTERFACE
        
        HRESULT ( STDMETHODCALLTYPE *QueryInterface )( 
            IEventBindings * This,
            /* [in] */ REFIID riid,
            /* [annotation][iid_is][out] */ 
            __RPC__deref_out  void **ppvObject);
        
        ULONG ( STDMETHODCALLTYPE *AddRef )( 
            IEventBindings * This);
        
        ULONG ( STDMETHODCALLTYPE *Release )( 
            IEventBindings * This);
        
        HRESULT ( STDMETHODCALLTYPE *GetTypeInfoCount )( 
            IEventBindings * This,
            /* [out] */ UINT *pctinfo);
        
        HRESULT ( STDMETHODCALLTYPE *GetTypeInfo )( 
            IEventBindings * This,
            /* [in] */ UINT iTInfo,
            /* [in] */ LCID lcid,
            /* [out] */ ITypeInfo **ppTInfo);
        
        HRESULT ( STDMETHODCALLTYPE *GetIDsOfNames )( 
            IEventBindings * This,
            /* [in] */ REFIID riid,
            /* [size_is][in] */ LPOLESTR *rgszNames,
            /* [range][in] */ UINT cNames,
            /* [in] */ LCID lcid,
            /* [size_is][out] */ DISPID *rgDispId);
        
        /* [local] */ HRESULT ( STDMETHODCALLTYPE *Invoke )( 
            IEventBindings * This,
            /* [in] */ DISPID dispIdMember,
            /* [in] */ REFIID riid,
            /* [in] */ LCID lcid,
            /* [in] */ WORD wFlags,
            /* [out][in] */ DISPPARAMS *pDispParams,
            /* [out] */ VARIANT *pVarResult,
            /* [out] */ EXCEPINFO *pExcepInfo,
            /* [out] */ UINT *puArgErr);
        
        /* [id][helpstring] */ HRESULT ( STDMETHODCALLTYPE *Item )( 
            IEventBindings * This,
            /* [in] */ VARIANT *pvarDesired,
            /* [retval][out] */ IEventBinding **ppEventBinding);
        
        /* [helpstring] */ HRESULT ( STDMETHODCALLTYPE *Add )( 
            IEventBindings * This,
            /* [in] */ BSTR strBinding,
            /* [retval][out] */ IEventBinding **ppBinding);
        
        /* [helpstring] */ HRESULT ( STDMETHODCALLTYPE *Remove )( 
            IEventBindings * This,
            /* [in] */ VARIANT *pvarDesired);
        
        /* [propget][helpstring] */ HRESULT ( STDMETHODCALLTYPE *get_Count )( 
            IEventBindings * This,
            /* [retval][out] */ long *plCount);
        
        /* [hidden][propget][id][helpstring] */ HRESULT ( STDMETHODCALLTYPE *get__NewEnum )( 
            IEventBindings * This,
            /* [retval][out] */ IUnknown **ppUnkEnum);
        
        END_INTERFACE
    } IEventBindingsVtbl;

    interface IEventBindings
    {
        CONST_VTBL struct IEventBindingsVtbl *lpVtbl;
    };

    

#ifdef COBJMACROS


#define IEventBindings_QueryInterface(This,riid,ppvObject)	\
    ( (This)->lpVtbl -> QueryInterface(This,riid,ppvObject) ) 

#define IEventBindings_AddRef(This)	\
    ( (This)->lpVtbl -> AddRef(This) ) 

#define IEventBindings_Release(This)	\
    ( (This)->lpVtbl -> Release(This) ) 


#define IEventBindings_GetTypeInfoCount(This,pctinfo)	\
    ( (This)->lpVtbl -> GetTypeInfoCount(This,pctinfo) ) 

#define IEventBindings_GetTypeInfo(This,iTInfo,lcid,ppTInfo)	\
    ( (This)->lpVtbl -> GetTypeInfo(This,iTInfo,lcid,ppTInfo) ) 

#define IEventBindings_GetIDsOfNames(This,riid,rgszNames,cNames,lcid,rgDispId)	\
    ( (This)->lpVtbl -> GetIDsOfNames(This,riid,rgszNames,cNames,lcid,rgDispId) ) 

#define IEventBindings_Invoke(This,dispIdMember,riid,lcid,wFlags,pDispParams,pVarResult,pExcepInfo,puArgErr)	\
    ( (This)->lpVtbl -> Invoke(This,dispIdMember,riid,lcid,wFlags,pDispParams,pVarResult,pExcepInfo,puArgErr) ) 


#define IEventBindings_Item(This,pvarDesired,ppEventBinding)	\
    ( (This)->lpVtbl -> Item(This,pvarDesired,ppEventBinding) ) 

#define IEventBindings_Add(This,strBinding,ppBinding)	\
    ( (This)->lpVtbl -> Add(This,strBinding,ppBinding) ) 

#define IEventBindings_Remove(This,pvarDesired)	\
    ( (This)->lpVtbl -> Remove(This,pvarDesired) ) 

#define IEventBindings_get_Count(This,plCount)	\
    ( (This)->lpVtbl -> get_Count(This,plCount) ) 

#define IEventBindings_get__NewEnum(This,ppUnkEnum)	\
    ( (This)->lpVtbl -> get__NewEnum(This,ppUnkEnum) ) 

#endif /* COBJMACROS */


#endif 	/* C style interface */




#endif 	/* __IEventBindings_INTERFACE_DEFINED__ */


#ifndef __IEventTypes_INTERFACE_DEFINED__
#define __IEventTypes_INTERFACE_DEFINED__

/* interface IEventTypes */
/* [uuid][unique][oleautomation][object][helpstring][dual] */ 


EXTERN_C const IID IID_IEventTypes;

#if defined(__cplusplus) && !defined(CINTERFACE)
    
    MIDL_INTERFACE("cab29ef0-a64f-11d0-a9ea-00aa00685c74")
    IEventTypes : public IDispatch
    {
    public:
        virtual /* [id][helpstring] */ HRESULT STDMETHODCALLTYPE Item( 
            /* [in] */ VARIANT *pvarDesired,
            /* [retval][out] */ IEventType **ppEventType) = 0;
        
        virtual /* [helpstring] */ HRESULT STDMETHODCALLTYPE Add( 
            /* [in] */ BSTR pszEventType) = 0;
        
        virtual /* [helpstring] */ HRESULT STDMETHODCALLTYPE Remove( 
            /* [in] */ BSTR pszEventType) = 0;
        
        virtual /* [propget][helpstring] */ HRESULT STDMETHODCALLTYPE get_Count( 
            /* [retval][out] */ long *plCount) = 0;
        
        virtual /* [hidden][propget][id][helpstring] */ HRESULT STDMETHODCALLTYPE get__NewEnum( 
            /* [retval][out] */ IUnknown **ppUnkEnum) = 0;
        
    };
    
#else 	/* C style interface */

    typedef struct IEventTypesVtbl
    {
        BEGIN_INTERFACE
        
        HRESULT ( STDMETHODCALLTYPE *QueryInterface )( 
            IEventTypes * This,
            /* [in] */ REFIID riid,
            /* [annotation][iid_is][out] */ 
            __RPC__deref_out  void **ppvObject);
        
        ULONG ( STDMETHODCALLTYPE *AddRef )( 
            IEventTypes * This);
        
        ULONG ( STDMETHODCALLTYPE *Release )( 
            IEventTypes * This);
        
        HRESULT ( STDMETHODCALLTYPE *GetTypeInfoCount )( 
            IEventTypes * This,
            /* [out] */ UINT *pctinfo);
        
        HRESULT ( STDMETHODCALLTYPE *GetTypeInfo )( 
            IEventTypes * This,
            /* [in] */ UINT iTInfo,
            /* [in] */ LCID lcid,
            /* [out] */ ITypeInfo **ppTInfo);
        
        HRESULT ( STDMETHODCALLTYPE *GetIDsOfNames )( 
            IEventTypes * This,
            /* [in] */ REFIID riid,
            /* [size_is][in] */ LPOLESTR *rgszNames,
            /* [range][in] */ UINT cNames,
            /* [in] */ LCID lcid,
            /* [size_is][out] */ DISPID *rgDispId);
        
        /* [local] */ HRESULT ( STDMETHODCALLTYPE *Invoke )( 
            IEventTypes * This,
            /* [in] */ DISPID dispIdMember,
            /* [in] */ REFIID riid,
            /* [in] */ LCID lcid,
            /* [in] */ WORD wFlags,
            /* [out][in] */ DISPPARAMS *pDispParams,
            /* [out] */ VARIANT *pVarResult,
            /* [out] */ EXCEPINFO *pExcepInfo,
            /* [out] */ UINT *puArgErr);
        
        /* [id][helpstring] */ HRESULT ( STDMETHODCALLTYPE *Item )( 
            IEventTypes * This,
            /* [in] */ VARIANT *pvarDesired,
            /* [retval][out] */ IEventType **ppEventType);
        
        /* [helpstring] */ HRESULT ( STDMETHODCALLTYPE *Add )( 
            IEventTypes * This,
            /* [in] */ BSTR pszEventType);
        
        /* [helpstring] */ HRESULT ( STDMETHODCALLTYPE *Remove )( 
            IEventTypes * This,
            /* [in] */ BSTR pszEventType);
        
        /* [propget][helpstring] */ HRESULT ( STDMETHODCALLTYPE *get_Count )( 
            IEventTypes * This,
            /* [retval][out] */ long *plCount);
        
        /* [hidden][propget][id][helpstring] */ HRESULT ( STDMETHODCALLTYPE *get__NewEnum )( 
            IEventTypes * This,
            /* [retval][out] */ IUnknown **ppUnkEnum);
        
        END_INTERFACE
    } IEventTypesVtbl;

    interface IEventTypes
    {
        CONST_VTBL struct IEventTypesVtbl *lpVtbl;
    };

    

#ifdef COBJMACROS


#define IEventTypes_QueryInterface(This,riid,ppvObject)	\
    ( (This)->lpVtbl -> QueryInterface(This,riid,ppvObject) ) 

#define IEventTypes_AddRef(This)	\
    ( (This)->lpVtbl -> AddRef(This) ) 

#define IEventTypes_Release(This)	\
    ( (This)->lpVtbl -> Release(This) ) 


#define IEventTypes_GetTypeInfoCount(This,pctinfo)	\
    ( (This)->lpVtbl -> GetTypeInfoCount(This,pctinfo) ) 

#define IEventTypes_GetTypeInfo(This,iTInfo,lcid,ppTInfo)	\
    ( (This)->lpVtbl -> GetTypeInfo(This,iTInfo,lcid,ppTInfo) ) 

#define IEventTypes_GetIDsOfNames(This,riid,rgszNames,cNames,lcid,rgDispId)	\
    ( (This)->lpVtbl -> GetIDsOfNames(This,riid,rgszNames,cNames,lcid,rgDispId) ) 

#define IEventTypes_Invoke(This,dispIdMember,riid,lcid,wFlags,pDispParams,pVarResult,pExcepInfo,puArgErr)	\
    ( (This)->lpVtbl -> Invoke(This,dispIdMember,riid,lcid,wFlags,pDispParams,pVarResult,pExcepInfo,puArgErr) ) 


#define IEventTypes_Item(This,pvarDesired,ppEventType)	\
    ( (This)->lpVtbl -> Item(This,pvarDesired,ppEventType) ) 

#define IEventTypes_Add(This,pszEventType)	\
    ( (This)->lpVtbl -> Add(This,pszEventType) ) 

#define IEventTypes_Remove(This,pszEventType)	\
    ( (This)->lpVtbl -> Remove(This,pszEventType) ) 

#define IEventTypes_get_Count(This,plCount)	\
    ( (This)->lpVtbl -> get_Count(This,plCount) ) 

#define IEventTypes_get__NewEnum(This,ppUnkEnum)	\
    ( (This)->lpVtbl -> get__NewEnum(This,ppUnkEnum) ) 

#endif /* COBJMACROS */


#endif 	/* C style interface */




#endif 	/* __IEventTypes_INTERFACE_DEFINED__ */


#ifndef __IEventBindingManager_INTERFACE_DEFINED__
#define __IEventBindingManager_INTERFACE_DEFINED__

/* interface IEventBindingManager */
/* [uuid][unique][oleautomation][object][helpstring][dual] */ 


EXTERN_C const IID IID_IEventBindingManager;

#if defined(__cplusplus) && !defined(CINTERFACE)
    
    MIDL_INTERFACE("0b4cdbc0-a64f-11d0-a9ea-00aa00685c74")
    IEventBindingManager : public IDispatch
    {
    public:
        virtual /* [propget][helpstring] */ HRESULT STDMETHODCALLTYPE get_Bindings( 
            /* [in] */ BSTR pszEventType,
            /* [retval][out] */ IEventBindings **ppBindings) = 0;
        
        virtual /* [hidden][propget][id][helpstring] */ HRESULT STDMETHODCALLTYPE get__NewEnum( 
            /* [retval][out] */ IUnknown **ppUnkEnum) = 0;
        
    };
    
#else 	/* C style interface */

    typedef struct IEventBindingManagerVtbl
    {
        BEGIN_INTERFACE
        
        HRESULT ( STDMETHODCALLTYPE *QueryInterface )( 
            IEventBindingManager * This,
            /* [in] */ REFIID riid,
            /* [annotation][iid_is][out] */ 
            __RPC__deref_out  void **ppvObject);
        
        ULONG ( STDMETHODCALLTYPE *AddRef )( 
            IEventBindingManager * This);
        
        ULONG ( STDMETHODCALLTYPE *Release )( 
            IEventBindingManager * This);
        
        HRESULT ( STDMETHODCALLTYPE *GetTypeInfoCount )( 
            IEventBindingManager * This,
            /* [out] */ UINT *pctinfo);
        
        HRESULT ( STDMETHODCALLTYPE *GetTypeInfo )( 
            IEventBindingManager * This,
            /* [in] */ UINT iTInfo,
            /* [in] */ LCID lcid,
            /* [out] */ ITypeInfo **ppTInfo);
        
        HRESULT ( STDMETHODCALLTYPE *GetIDsOfNames )( 
            IEventBindingManager * This,
            /* [in] */ REFIID riid,
            /* [size_is][in] */ LPOLESTR *rgszNames,
            /* [range][in] */ UINT cNames,
            /* [in] */ LCID lcid,
            /* [size_is][out] */ DISPID *rgDispId);
        
        /* [local] */ HRESULT ( STDMETHODCALLTYPE *Invoke )( 
            IEventBindingManager * This,
            /* [in] */ DISPID dispIdMember,
            /* [in] */ REFIID riid,
            /* [in] */ LCID lcid,
            /* [in] */ WORD wFlags,
            /* [out][in] */ DISPPARAMS *pDispParams,
            /* [out] */ VARIANT *pVarResult,
            /* [out] */ EXCEPINFO *pExcepInfo,
            /* [out] */ UINT *puArgErr);
        
        /* [propget][helpstring] */ HRESULT ( STDMETHODCALLTYPE *get_Bindings )( 
            IEventBindingManager * This,
            /* [in] */ BSTR pszEventType,
            /* [retval][out] */ IEventBindings **ppBindings);
        
        /* [hidden][propget][id][helpstring] */ HRESULT ( STDMETHODCALLTYPE *get__NewEnum )( 
            IEventBindingManager * This,
            /* [retval][out] */ IUnknown **ppUnkEnum);
        
        END_INTERFACE
    } IEventBindingManagerVtbl;

    interface IEventBindingManager
    {
        CONST_VTBL struct IEventBindingManagerVtbl *lpVtbl;
    };

    

#ifdef COBJMACROS


#define IEventBindingManager_QueryInterface(This,riid,ppvObject)	\
    ( (This)->lpVtbl -> QueryInterface(This,riid,ppvObject) ) 

#define IEventBindingManager_AddRef(This)	\
    ( (This)->lpVtbl -> AddRef(This) ) 

#define IEventBindingManager_Release(This)	\
    ( (This)->lpVtbl -> Release(This) ) 


#define IEventBindingManager_GetTypeInfoCount(This,pctinfo)	\
    ( (This)->lpVtbl -> GetTypeInfoCount(This,pctinfo) ) 

#define IEventBindingManager_GetTypeInfo(This,iTInfo,lcid,ppTInfo)	\
    ( (This)->lpVtbl -> GetTypeInfo(This,iTInfo,lcid,ppTInfo) ) 

#define IEventBindingManager_GetIDsOfNames(This,riid,rgszNames,cNames,lcid,rgDispId)	\
    ( (This)->lpVtbl -> GetIDsOfNames(This,riid,rgszNames,cNames,lcid,rgDispId) ) 

#define IEventBindingManager_Invoke(This,dispIdMember,riid,lcid,wFlags,pDispParams,pVarResult,pExcepInfo,puArgErr)	\
    ( (This)->lpVtbl -> Invoke(This,dispIdMember,riid,lcid,wFlags,pDispParams,pVarResult,pExcepInfo,puArgErr) ) 


#define IEventBindingManager_get_Bindings(This,pszEventType,ppBindings)	\
    ( (This)->lpVtbl -> get_Bindings(This,pszEventType,ppBindings) ) 

#define IEventBindingManager_get__NewEnum(This,ppUnkEnum)	\
    ( (This)->lpVtbl -> get__NewEnum(This,ppUnkEnum) ) 

#endif /* COBJMACROS */


#endif 	/* C style interface */




#endif 	/* __IEventBindingManager_INTERFACE_DEFINED__ */


#ifndef __IEventBindingManagerCopier_INTERFACE_DEFINED__
#define __IEventBindingManagerCopier_INTERFACE_DEFINED__

/* interface IEventBindingManagerCopier */
/* [uuid][unique][oleautomation][object][hidden][helpstring][dual] */ 


EXTERN_C const IID IID_IEventBindingManagerCopier;

#if defined(__cplusplus) && !defined(CINTERFACE)
    
    MIDL_INTERFACE("64bad540-f88d-11d0-aa14-00aa006bc80b")
    IEventBindingManagerCopier : public IDispatch
    {
    public:
        virtual /* [helpstring] */ HRESULT STDMETHODCALLTYPE Copy( 
            /* [in] */ long lTimeout,
            /* [retval][out] */ IEventBindingManager **ppBindingManager) = 0;
        
        virtual /* [helpstring] */ HRESULT STDMETHODCALLTYPE EmptyCopy( 
            /* [retval][out] */ IEventBindingManager **ppBindingManager) = 0;
        
    };
    
#else 	/* C style interface */

    typedef struct IEventBindingManagerCopierVtbl
    {
        BEGIN_INTERFACE
        
        HRESULT ( STDMETHODCALLTYPE *QueryInterface )( 
            IEventBindingManagerCopier * This,
            /* [in] */ REFIID riid,
            /* [annotation][iid_is][out] */ 
            __RPC__deref_out  void **ppvObject);
        
        ULONG ( STDMETHODCALLTYPE *AddRef )( 
            IEventBindingManagerCopier * This);
        
        ULONG ( STDMETHODCALLTYPE *Release )( 
            IEventBindingManagerCopier * This);
        
        HRESULT ( STDMETHODCALLTYPE *GetTypeInfoCount )( 
            IEventBindingManagerCopier * This,
            /* [out] */ UINT *pctinfo);
        
        HRESULT ( STDMETHODCALLTYPE *GetTypeInfo )( 
            IEventBindingManagerCopier * This,
            /* [in] */ UINT iTInfo,
            /* [in] */ LCID lcid,
            /* [out] */ ITypeInfo **ppTInfo);
        
        HRESULT ( STDMETHODCALLTYPE *GetIDsOfNames )( 
            IEventBindingManagerCopier * This,
            /* [in] */ REFIID riid,
            /* [size_is][in] */ LPOLESTR *rgszNames,
            /* [range][in] */ UINT cNames,
            /* [in] */ LCID lcid,
            /* [size_is][out] */ DISPID *rgDispId);
        
        /* [local] */ HRESULT ( STDMETHODCALLTYPE *Invoke )( 
            IEventBindingManagerCopier * This,
            /* [in] */ DISPID dispIdMember,
            /* [in] */ REFIID riid,
            /* [in] */ LCID lcid,
            /* [in] */ WORD wFlags,
            /* [out][in] */ DISPPARAMS *pDispParams,
            /* [out] */ VARIANT *pVarResult,
            /* [out] */ EXCEPINFO *pExcepInfo,
            /* [out] */ UINT *puArgErr);
        
        /* [helpstring] */ HRESULT ( STDMETHODCALLTYPE *Copy )( 
            IEventBindingManagerCopier * This,
            /* [in] */ long lTimeout,
            /* [retval][out] */ IEventBindingManager **ppBindingManager);
        
        /* [helpstring] */ HRESULT ( STDMETHODCALLTYPE *EmptyCopy )( 
            IEventBindingManagerCopier * This,
            /* [retval][out] */ IEventBindingManager **ppBindingManager);
        
        END_INTERFACE
    } IEventBindingManagerCopierVtbl;

    interface IEventBindingManagerCopier
    {
        CONST_VTBL struct IEventBindingManagerCopierVtbl *lpVtbl;
    };

    

#ifdef COBJMACROS


#define IEventBindingManagerCopier_QueryInterface(This,riid,ppvObject)	\
    ( (This)->lpVtbl -> QueryInterface(This,riid,ppvObject) ) 

#define IEventBindingManagerCopier_AddRef(This)	\
    ( (This)->lpVtbl -> AddRef(This) ) 

#define IEventBindingManagerCopier_Release(This)	\
    ( (This)->lpVtbl -> Release(This) ) 


#define IEventBindingManagerCopier_GetTypeInfoCount(This,pctinfo)	\
    ( (This)->lpVtbl -> GetTypeInfoCount(This,pctinfo) ) 

#define IEventBindingManagerCopier_GetTypeInfo(This,iTInfo,lcid,ppTInfo)	\
    ( (This)->lpVtbl -> GetTypeInfo(This,iTInfo,lcid,ppTInfo) ) 

#define IEventBindingManagerCopier_GetIDsOfNames(This,riid,rgszNames,cNames,lcid,rgDispId)	\
    ( (This)->lpVtbl -> GetIDsOfNames(This,riid,rgszNames,cNames,lcid,rgDispId) ) 

#define IEventBindingManagerCopier_Invoke(This,dispIdMember,riid,lcid,wFlags,pDispParams,pVarResult,pExcepInfo,puArgErr)	\
    ( (This)->lpVtbl -> Invoke(This,dispIdMember,riid,lcid,wFlags,pDispParams,pVarResult,pExcepInfo,puArgErr) ) 


#define IEventBindingManagerCopier_Copy(This,lTimeout,ppBindingManager)	\
    ( (This)->lpVtbl -> Copy(This,lTimeout,ppBindingManager) ) 

#define IEventBindingManagerCopier_EmptyCopy(This,ppBindingManager)	\
    ( (This)->lpVtbl -> EmptyCopy(This,ppBindingManager) ) 

#endif /* COBJMACROS */


#endif 	/* C style interface */




#endif 	/* __IEventBindingManagerCopier_INTERFACE_DEFINED__ */


/* interface __MIDL_itf_seo_0000_0019 */
/* [local] */ 




extern RPC_IF_HANDLE __MIDL_itf_seo_0000_0019_v0_0_c_ifspec;
extern RPC_IF_HANDLE __MIDL_itf_seo_0000_0019_v0_0_s_ifspec;

#ifndef __IEventRouter_INTERFACE_DEFINED__
#define __IEventRouter_INTERFACE_DEFINED__

/* interface IEventRouter */
/* [uuid][unique][object][hidden][helpstring] */ 


EXTERN_C const IID IID_IEventRouter;

#if defined(__cplusplus) && !defined(CINTERFACE)
    
    MIDL_INTERFACE("1a00b970-eda0-11d0-aa10-00aa006bc80b")
    IEventRouter : public IUnknown
    {
    public:
        virtual /* [propget][helpstring] */ HRESULT STDMETHODCALLTYPE get_Database( 
            /* [retval][out] */ IEventBindingManager **ppBindingManager) = 0;
        
        virtual /* [propput][helpstring] */ HRESULT STDMETHODCALLTYPE put_Database( 
            /* [unique][in] */ IEventBindingManager *pBindingManager) = 0;
        
        virtual /* [propputref][helpstring] */ HRESULT STDMETHODCALLTYPE putref_Database( 
            /* [unique][in] */ IEventBindingManager **ppBindingManager) = 0;
        
        virtual /* [helpstring] */ HRESULT STDMETHODCALLTYPE GetDispatcher( 
            /* [in] */ REFIID iidEvent,
            /* [in] */ REFIID iidDesired,
            /* [retval][iid_is][out] */ IUnknown **ppUnkResult) = 0;
        
        virtual /* [helpstring] */ HRESULT STDMETHODCALLTYPE GetDispatcherByCLSID( 
            /* [in] */ REFCLSID clsidDispatcher,
            /* [in] */ REFIID iidEvent,
            /* [in] */ REFIID iidDesired,
            /* [retval][iid_is][out] */ IUnknown **ppUnkResult) = 0;
        
        virtual /* [helpstring] */ HRESULT STDMETHODCALLTYPE GetDispatcherByClassFactory( 
            /* [in] */ REFCLSID clsidDispatcher,
            /* [in] */ IClassFactory *piClassFactory,
            /* [in] */ REFIID iidEvent,
            /* [in] */ REFIID iidDesired,
            /* [retval][iid_is][out] */ IUnknown **ppUnkResult) = 0;
        
    };
    
#else 	/* C style interface */

    typedef struct IEventRouterVtbl
    {
        BEGIN_INTERFACE
        
        HRESULT ( STDMETHODCALLTYPE *QueryInterface )( 
            IEventRouter * This,
            /* [in] */ REFIID riid,
            /* [annotation][iid_is][out] */ 
            __RPC__deref_out  void **ppvObject);
        
        ULONG ( STDMETHODCALLTYPE *AddRef )( 
            IEventRouter * This);
        
        ULONG ( STDMETHODCALLTYPE *Release )( 
            IEventRouter * This);
        
        /* [propget][helpstring] */ HRESULT ( STDMETHODCALLTYPE *get_Database )( 
            IEventRouter * This,
            /* [retval][out] */ IEventBindingManager **ppBindingManager);
        
        /* [propput][helpstring] */ HRESULT ( STDMETHODCALLTYPE *put_Database )( 
            IEventRouter * This,
            /* [unique][in] */ IEventBindingManager *pBindingManager);
        
        /* [propputref][helpstring] */ HRESULT ( STDMETHODCALLTYPE *putref_Database )( 
            IEventRouter * This,
            /* [unique][in] */ IEventBindingManager **ppBindingManager);
        
        /* [helpstring] */ HRESULT ( STDMETHODCALLTYPE *GetDispatcher )( 
            IEventRouter * This,
            /* [in] */ REFIID iidEvent,
            /* [in] */ REFIID iidDesired,
            /* [retval][iid_is][out] */ IUnknown **ppUnkResult);
        
        /* [helpstring] */ HRESULT ( STDMETHODCALLTYPE *GetDispatcherByCLSID )( 
            IEventRouter * This,
            /* [in] */ REFCLSID clsidDispatcher,
            /* [in] */ REFIID iidEvent,
            /* [in] */ REFIID iidDesired,
            /* [retval][iid_is][out] */ IUnknown **ppUnkResult);
        
        /* [helpstring] */ HRESULT ( STDMETHODCALLTYPE *GetDispatcherByClassFactory )( 
            IEventRouter * This,
            /* [in] */ REFCLSID clsidDispatcher,
            /* [in] */ IClassFactory *piClassFactory,
            /* [in] */ REFIID iidEvent,
            /* [in] */ REFIID iidDesired,
            /* [retval][iid_is][out] */ IUnknown **ppUnkResult);
        
        END_INTERFACE
    } IEventRouterVtbl;

    interface IEventRouter
    {
        CONST_VTBL struct IEventRouterVtbl *lpVtbl;
    };

    

#ifdef COBJMACROS


#define IEventRouter_QueryInterface(This,riid,ppvObject)	\
    ( (This)->lpVtbl -> QueryInterface(This,riid,ppvObject) ) 

#define IEventRouter_AddRef(This)	\
    ( (This)->lpVtbl -> AddRef(This) ) 

#define IEventRouter_Release(This)	\
    ( (This)->lpVtbl -> Release(This) ) 


#define IEventRouter_get_Database(This,ppBindingManager)	\
    ( (This)->lpVtbl -> get_Database(This,ppBindingManager) ) 

#define IEventRouter_put_Database(This,pBindingManager)	\
    ( (This)->lpVtbl -> put_Database(This,pBindingManager) ) 

#define IEventRouter_putref_Database(This,ppBindingManager)	\
    ( (This)->lpVtbl -> putref_Database(This,ppBindingManager) ) 

#define IEventRouter_GetDispatcher(This,iidEvent,iidDesired,ppUnkResult)	\
    ( (This)->lpVtbl -> GetDispatcher(This,iidEvent,iidDesired,ppUnkResult) ) 

#define IEventRouter_GetDispatcherByCLSID(This,clsidDispatcher,iidEvent,iidDesired,ppUnkResult)	\
    ( (This)->lpVtbl -> GetDispatcherByCLSID(This,clsidDispatcher,iidEvent,iidDesired,ppUnkResult) ) 

#define IEventRouter_GetDispatcherByClassFactory(This,clsidDispatcher,piClassFactory,iidEvent,iidDesired,ppUnkResult)	\
    ( (This)->lpVtbl -> GetDispatcherByClassFactory(This,clsidDispatcher,piClassFactory,iidEvent,iidDesired,ppUnkResult) ) 

#endif /* COBJMACROS */


#endif 	/* C style interface */




#endif 	/* __IEventRouter_INTERFACE_DEFINED__ */


#ifndef __IEventDispatcher_INTERFACE_DEFINED__
#define __IEventDispatcher_INTERFACE_DEFINED__

/* interface IEventDispatcher */
/* [uuid][unique][object][hidden][helpstring] */ 


EXTERN_C const IID IID_IEventDispatcher;

#if defined(__cplusplus) && !defined(CINTERFACE)
    
    MIDL_INTERFACE("c980f550-ed9e-11d0-aa10-00aa006bc80b")
    IEventDispatcher : public IUnknown
    {
    public:
        virtual /* [helpstring] */ HRESULT STDMETHODCALLTYPE SetContext( 
            /* [in] */ REFGUID guidEventType,
            /* [in] */ IEventRouter *piRouter,
            /* [in] */ IEventBindings *pBindings) = 0;
        
    };
    
#else 	/* C style interface */

    typedef struct IEventDispatcherVtbl
    {
        BEGIN_INTERFACE
        
        HRESULT ( STDMETHODCALLTYPE *QueryInterface )( 
            IEventDispatcher * This,
            /* [in] */ REFIID riid,
            /* [annotation][iid_is][out] */ 
            __RPC__deref_out  void **ppvObject);
        
        ULONG ( STDMETHODCALLTYPE *AddRef )( 
            IEventDispatcher * This);
        
        ULONG ( STDMETHODCALLTYPE *Release )( 
            IEventDispatcher * This);
        
        /* [helpstring] */ HRESULT ( STDMETHODCALLTYPE *SetContext )( 
            IEventDispatcher * This,
            /* [in] */ REFGUID guidEventType,
            /* [in] */ IEventRouter *piRouter,
            /* [in] */ IEventBindings *pBindings);
        
        END_INTERFACE
    } IEventDispatcherVtbl;

    interface IEventDispatcher
    {
        CONST_VTBL struct IEventDispatcherVtbl *lpVtbl;
    };

    

#ifdef COBJMACROS


#define IEventDispatcher_QueryInterface(This,riid,ppvObject)	\
    ( (This)->lpVtbl -> QueryInterface(This,riid,ppvObject) ) 

#define IEventDispatcher_AddRef(This)	\
    ( (This)->lpVtbl -> AddRef(This) ) 

#define IEventDispatcher_Release(This)	\
    ( (This)->lpVtbl -> Release(This) ) 


#define IEventDispatcher_SetContext(This,guidEventType,piRouter,pBindings)	\
    ( (This)->lpVtbl -> SetContext(This,guidEventType,piRouter,pBindings) ) 

#endif /* COBJMACROS */


#endif 	/* C style interface */




#endif 	/* __IEventDispatcher_INTERFACE_DEFINED__ */


/* interface __MIDL_itf_seo_0000_0021 */
/* [local] */ 




extern RPC_IF_HANDLE __MIDL_itf_seo_0000_0021_v0_0_c_ifspec;
extern RPC_IF_HANDLE __MIDL_itf_seo_0000_0021_v0_0_s_ifspec;

#ifndef __IEventSource_INTERFACE_DEFINED__
#define __IEventSource_INTERFACE_DEFINED__

/* interface IEventSource */
/* [uuid][unique][oleautomation][object][helpstring][dual] */ 


EXTERN_C const IID IID_IEventSource;

#if defined(__cplusplus) && !defined(CINTERFACE)
    
    MIDL_INTERFACE("b1dcb040-a652-11d0-a9ea-00aa00685c74")
    IEventSource : public IDispatch
    {
    public:
        virtual /* [propget][helpstring] */ HRESULT STDMETHODCALLTYPE get_ID( 
            /* [retval][out] */ BSTR *pstrID) = 0;
        
        virtual /* [propget][helpstring] */ HRESULT STDMETHODCALLTYPE get_DisplayName( 
            /* [retval][out] */ BSTR *pstrDisplayName) = 0;
        
        virtual /* [propput][helpstring] */ HRESULT STDMETHODCALLTYPE put_DisplayName( 
            /* [in] */ BSTR pszDisplayName) = 0;
        
        virtual /* [propputref][helpstring] */ HRESULT STDMETHODCALLTYPE putref_DisplayName( 
            /* [in] */ BSTR *ppszDisplayName) = 0;
        
        virtual /* [propget][helpstring] */ HRESULT STDMETHODCALLTYPE get_BindingManagerMoniker( 
            /* [retval][out] */ IUnknown **ppUnkMoniker) = 0;
        
        virtual /* [propput][helpstring] */ HRESULT STDMETHODCALLTYPE put_BindingManagerMoniker( 
            /* [in] */ IUnknown *pUnkMoniker) = 0;
        
        virtual /* [propputref][helpstring] */ HRESULT STDMETHODCALLTYPE putref_BindingManagerMoniker( 
            /* [in] */ IUnknown **ppUnkMoniker) = 0;
        
        virtual /* [helpstring] */ HRESULT STDMETHODCALLTYPE GetBindingManager( 
            /* [retval][out] */ IEventBindingManager **ppBindingManager) = 0;
        
        virtual /* [propget][helpstring] */ HRESULT STDMETHODCALLTYPE get_Properties( 
            /* [retval][out] */ IEventPropertyBag **ppProperties) = 0;
        
        virtual /* [helpstring] */ HRESULT STDMETHODCALLTYPE Save( void) = 0;
        
    };
    
#else 	/* C style interface */

    typedef struct IEventSourceVtbl
    {
        BEGIN_INTERFACE
        
        HRESULT ( STDMETHODCALLTYPE *QueryInterface )( 
            IEventSource * This,
            /* [in] */ REFIID riid,
            /* [annotation][iid_is][out] */ 
            __RPC__deref_out  void **ppvObject);
        
        ULONG ( STDMETHODCALLTYPE *AddRef )( 
            IEventSource * This);
        
        ULONG ( STDMETHODCALLTYPE *Release )( 
            IEventSource * This);
        
        HRESULT ( STDMETHODCALLTYPE *GetTypeInfoCount )( 
            IEventSource * This,
            /* [out] */ UINT *pctinfo);
        
        HRESULT ( STDMETHODCALLTYPE *GetTypeInfo )( 
            IEventSource * This,
            /* [in] */ UINT iTInfo,
            /* [in] */ LCID lcid,
            /* [out] */ ITypeInfo **ppTInfo);
        
        HRESULT ( STDMETHODCALLTYPE *GetIDsOfNames )( 
            IEventSource * This,
            /* [in] */ REFIID riid,
            /* [size_is][in] */ LPOLESTR *rgszNames,
            /* [range][in] */ UINT cNames,
            /* [in] */ LCID lcid,
            /* [size_is][out] */ DISPID *rgDispId);
        
        /* [local] */ HRESULT ( STDMETHODCALLTYPE *Invoke )( 
            IEventSource * This,
            /* [in] */ DISPID dispIdMember,
            /* [in] */ REFIID riid,
            /* [in] */ LCID lcid,
            /* [in] */ WORD wFlags,
            /* [out][in] */ DISPPARAMS *pDispParams,
            /* [out] */ VARIANT *pVarResult,
            /* [out] */ EXCEPINFO *pExcepInfo,
            /* [out] */ UINT *puArgErr);
        
        /* [propget][helpstring] */ HRESULT ( STDMETHODCALLTYPE *get_ID )( 
            IEventSource * This,
            /* [retval][out] */ BSTR *pstrID);
        
        /* [propget][helpstring] */ HRESULT ( STDMETHODCALLTYPE *get_DisplayName )( 
            IEventSource * This,
            /* [retval][out] */ BSTR *pstrDisplayName);
        
        /* [propput][helpstring] */ HRESULT ( STDMETHODCALLTYPE *put_DisplayName )( 
            IEventSource * This,
            /* [in] */ BSTR pszDisplayName);
        
        /* [propputref][helpstring] */ HRESULT ( STDMETHODCALLTYPE *putref_DisplayName )( 
            IEventSource * This,
            /* [in] */ BSTR *ppszDisplayName);
        
        /* [propget][helpstring] */ HRESULT ( STDMETHODCALLTYPE *get_BindingManagerMoniker )( 
            IEventSource * This,
            /* [retval][out] */ IUnknown **ppUnkMoniker);
        
        /* [propput][helpstring] */ HRESULT ( STDMETHODCALLTYPE *put_BindingManagerMoniker )( 
            IEventSource * This,
            /* [in] */ IUnknown *pUnkMoniker);
        
        /* [propputref][helpstring] */ HRESULT ( STDMETHODCALLTYPE *putref_BindingManagerMoniker )( 
            IEventSource * This,
            /* [in] */ IUnknown **ppUnkMoniker);
        
        /* [helpstring] */ HRESULT ( STDMETHODCALLTYPE *GetBindingManager )( 
            IEventSource * This,
            /* [retval][out] */ IEventBindingManager **ppBindingManager);
        
        /* [propget][helpstring] */ HRESULT ( STDMETHODCALLTYPE *get_Properties )( 
            IEventSource * This,
            /* [retval][out] */ IEventPropertyBag **ppProperties);
        
        /* [helpstring] */ HRESULT ( STDMETHODCALLTYPE *Save )( 
            IEventSource * This);
        
        END_INTERFACE
    } IEventSourceVtbl;

    interface IEventSource
    {
        CONST_VTBL struct IEventSourceVtbl *lpVtbl;
    };

    

#ifdef COBJMACROS


#define IEventSource_QueryInterface(This,riid,ppvObject)	\
    ( (This)->lpVtbl -> QueryInterface(This,riid,ppvObject) ) 

#define IEventSource_AddRef(This)	\
    ( (This)->lpVtbl -> AddRef(This) ) 

#define IEventSource_Release(This)	\
    ( (This)->lpVtbl -> Release(This) ) 


#define IEventSource_GetTypeInfoCount(This,pctinfo)	\
    ( (This)->lpVtbl -> GetTypeInfoCount(This,pctinfo) ) 

#define IEventSource_GetTypeInfo(This,iTInfo,lcid,ppTInfo)	\
    ( (This)->lpVtbl -> GetTypeInfo(This,iTInfo,lcid,ppTInfo) ) 

#define IEventSource_GetIDsOfNames(This,riid,rgszNames,cNames,lcid,rgDispId)	\
    ( (This)->lpVtbl -> GetIDsOfNames(This,riid,rgszNames,cNames,lcid,rgDispId) ) 

#define IEventSource_Invoke(This,dispIdMember,riid,lcid,wFlags,pDispParams,pVarResult,pExcepInfo,puArgErr)	\
    ( (This)->lpVtbl -> Invoke(This,dispIdMember,riid,lcid,wFlags,pDispParams,pVarResult,pExcepInfo,puArgErr) ) 


#define IEventSource_get_ID(This,pstrID)	\
    ( (This)->lpVtbl -> get_ID(This,pstrID) ) 

#define IEventSource_get_DisplayName(This,pstrDisplayName)	\
    ( (This)->lpVtbl -> get_DisplayName(This,pstrDisplayName) ) 

#define IEventSource_put_DisplayName(This,pszDisplayName)	\
    ( (This)->lpVtbl -> put_DisplayName(This,pszDisplayName) ) 

#define IEventSource_putref_DisplayName(This,ppszDisplayName)	\
    ( (This)->lpVtbl -> putref_DisplayName(This,ppszDisplayName) ) 

#define IEventSource_get_BindingManagerMoniker(This,ppUnkMoniker)	\
    ( (This)->lpVtbl -> get_BindingManagerMoniker(This,ppUnkMoniker) ) 

#define IEventSource_put_BindingManagerMoniker(This,pUnkMoniker)	\
    ( (This)->lpVtbl -> put_BindingManagerMoniker(This,pUnkMoniker) ) 

#define IEventSource_putref_BindingManagerMoniker(This,ppUnkMoniker)	\
    ( (This)->lpVtbl -> putref_BindingManagerMoniker(This,ppUnkMoniker) ) 

#define IEventSource_GetBindingManager(This,ppBindingManager)	\
    ( (This)->lpVtbl -> GetBindingManager(This,ppBindingManager) ) 

#define IEventSource_get_Properties(This,ppProperties)	\
    ( (This)->lpVtbl -> get_Properties(This,ppProperties) ) 

#define IEventSource_Save(This)	\
    ( (This)->lpVtbl -> Save(This) ) 

#endif /* COBJMACROS */


#endif 	/* C style interface */




#endif 	/* __IEventSource_INTERFACE_DEFINED__ */


#ifndef __IEventSources_INTERFACE_DEFINED__
#define __IEventSources_INTERFACE_DEFINED__

/* interface IEventSources */
/* [uuid][unique][oleautomation][object][helpstring][dual] */ 


EXTERN_C const IID IID_IEventSources;

#if defined(__cplusplus) && !defined(CINTERFACE)
    
    MIDL_INTERFACE("73e8c930-a652-11d0-a9ea-00aa00685c74")
    IEventSources : public IDispatch
    {
    public:
        virtual /* [id][helpstring] */ HRESULT STDMETHODCALLTYPE Item( 
            /* [in] */ VARIANT *pvarDesired,
            /* [retval][out] */ IEventSource **ppSource) = 0;
        
        virtual /* [helpstring] */ HRESULT STDMETHODCALLTYPE Add( 
            /* [in] */ BSTR pszSource,
            /* [retval][out] */ IEventSource **ppSource) = 0;
        
        virtual /* [helpstring] */ HRESULT STDMETHODCALLTYPE Remove( 
            /* [in] */ VARIANT *pvarDesired) = 0;
        
        virtual /* [propget][helpstring] */ HRESULT STDMETHODCALLTYPE get_Count( 
            /* [retval][out] */ long *plCount) = 0;
        
        virtual /* [hidden][propget][id][helpstring] */ HRESULT STDMETHODCALLTYPE get__NewEnum( 
            /* [retval][out] */ IUnknown **ppUnkEnum) = 0;
        
    };
    
#else 	/* C style interface */

    typedef struct IEventSourcesVtbl
    {
        BEGIN_INTERFACE
        
        HRESULT ( STDMETHODCALLTYPE *QueryInterface )( 
            IEventSources * This,
            /* [in] */ REFIID riid,
            /* [annotation][iid_is][out] */ 
            __RPC__deref_out  void **ppvObject);
        
        ULONG ( STDMETHODCALLTYPE *AddRef )( 
            IEventSources * This);
        
        ULONG ( STDMETHODCALLTYPE *Release )( 
            IEventSources * This);
        
        HRESULT ( STDMETHODCALLTYPE *GetTypeInfoCount )( 
            IEventSources * This,
            /* [out] */ UINT *pctinfo);
        
        HRESULT ( STDMETHODCALLTYPE *GetTypeInfo )( 
            IEventSources * This,
            /* [in] */ UINT iTInfo,
            /* [in] */ LCID lcid,
            /* [out] */ ITypeInfo **ppTInfo);
        
        HRESULT ( STDMETHODCALLTYPE *GetIDsOfNames )( 
            IEventSources * This,
            /* [in] */ REFIID riid,
            /* [size_is][in] */ LPOLESTR *rgszNames,
            /* [range][in] */ UINT cNames,
            /* [in] */ LCID lcid,
            /* [size_is][out] */ DISPID *rgDispId);
        
        /* [local] */ HRESULT ( STDMETHODCALLTYPE *Invoke )( 
            IEventSources * This,
            /* [in] */ DISPID dispIdMember,
            /* [in] */ REFIID riid,
            /* [in] */ LCID lcid,
            /* [in] */ WORD wFlags,
            /* [out][in] */ DISPPARAMS *pDispParams,
            /* [out] */ VARIANT *pVarResult,
            /* [out] */ EXCEPINFO *pExcepInfo,
            /* [out] */ UINT *puArgErr);
        
        /* [id][helpstring] */ HRESULT ( STDMETHODCALLTYPE *Item )( 
            IEventSources * This,
            /* [in] */ VARIANT *pvarDesired,
            /* [retval][out] */ IEventSource **ppSource);
        
        /* [helpstring] */ HRESULT ( STDMETHODCALLTYPE *Add )( 
            IEventSources * This,
            /* [in] */ BSTR pszSource,
            /* [retval][out] */ IEventSource **ppSource);
        
        /* [helpstring] */ HRESULT ( STDMETHODCALLTYPE *Remove )( 
            IEventSources * This,
            /* [in] */ VARIANT *pvarDesired);
        
        /* [propget][helpstring] */ HRESULT ( STDMETHODCALLTYPE *get_Count )( 
            IEventSources * This,
            /* [retval][out] */ long *plCount);
        
        /* [hidden][propget][id][helpstring] */ HRESULT ( STDMETHODCALLTYPE *get__NewEnum )( 
            IEventSources * This,
            /* [retval][out] */ IUnknown **ppUnkEnum);
        
        END_INTERFACE
    } IEventSourcesVtbl;

    interface IEventSources
    {
        CONST_VTBL struct IEventSourcesVtbl *lpVtbl;
    };

    

#ifdef COBJMACROS


#define IEventSources_QueryInterface(This,riid,ppvObject)	\
    ( (This)->lpVtbl -> QueryInterface(This,riid,ppvObject) ) 

#define IEventSources_AddRef(This)	\
    ( (This)->lpVtbl -> AddRef(This) ) 

#define IEventSources_Release(This)	\
    ( (This)->lpVtbl -> Release(This) ) 


#define IEventSources_GetTypeInfoCount(This,pctinfo)	\
    ( (This)->lpVtbl -> GetTypeInfoCount(This,pctinfo) ) 

#define IEventSources_GetTypeInfo(This,iTInfo,lcid,ppTInfo)	\
    ( (This)->lpVtbl -> GetTypeInfo(This,iTInfo,lcid,ppTInfo) ) 

#define IEventSources_GetIDsOfNames(This,riid,rgszNames,cNames,lcid,rgDispId)	\
    ( (This)->lpVtbl -> GetIDsOfNames(This,riid,rgszNames,cNames,lcid,rgDispId) ) 

#define IEventSources_Invoke(This,dispIdMember,riid,lcid,wFlags,pDispParams,pVarResult,pExcepInfo,puArgErr)	\
    ( (This)->lpVtbl -> Invoke(This,dispIdMember,riid,lcid,wFlags,pDispParams,pVarResult,pExcepInfo,puArgErr) ) 


#define IEventSources_Item(This,pvarDesired,ppSource)	\
    ( (This)->lpVtbl -> Item(This,pvarDesired,ppSource) ) 

#define IEventSources_Add(This,pszSource,ppSource)	\
    ( (This)->lpVtbl -> Add(This,pszSource,ppSource) ) 

#define IEventSources_Remove(This,pvarDesired)	\
    ( (This)->lpVtbl -> Remove(This,pvarDesired) ) 

#define IEventSources_get_Count(This,plCount)	\
    ( (This)->lpVtbl -> get_Count(This,plCount) ) 

#define IEventSources_get__NewEnum(This,ppUnkEnum)	\
    ( (This)->lpVtbl -> get__NewEnum(This,ppUnkEnum) ) 

#endif /* COBJMACROS */


#endif 	/* C style interface */




#endif 	/* __IEventSources_INTERFACE_DEFINED__ */


#ifndef __IEventSourceType_INTERFACE_DEFINED__
#define __IEventSourceType_INTERFACE_DEFINED__

/* interface IEventSourceType */
/* [uuid][unique][oleautomation][object][helpstring][dual] */ 


EXTERN_C const IID IID_IEventSourceType;

#if defined(__cplusplus) && !defined(CINTERFACE)
    
    MIDL_INTERFACE("063a62e0-a652-11d0-a9ea-00aa00685c74")
    IEventSourceType : public IDispatch
    {
    public:
        virtual /* [propget][helpstring] */ HRESULT STDMETHODCALLTYPE get_ID( 
            /* [retval][out] */ BSTR *pstrID) = 0;
        
        virtual /* [propget][helpstring] */ HRESULT STDMETHODCALLTYPE get_DisplayName( 
            /* [retval][out] */ BSTR *pstrDisplayName) = 0;
        
        virtual /* [propput][helpstring] */ HRESULT STDMETHODCALLTYPE put_DisplayName( 
            /* [in] */ BSTR pszDisplayName) = 0;
        
        virtual /* [propputref][helpstring] */ HRESULT STDMETHODCALLTYPE putref_DisplayName( 
            /* [in] */ BSTR *ppszDisplayName) = 0;
        
        virtual /* [propget][helpstring] */ HRESULT STDMETHODCALLTYPE get_EventTypes( 
            /* [retval][out] */ IEventTypes **ppEventTypes) = 0;
        
        virtual /* [propget][helpstring] */ HRESULT STDMETHODCALLTYPE get_Sources( 
            /* [retval][out] */ IEventSources **ppSources) = 0;
        
        virtual /* [helpstring] */ HRESULT STDMETHODCALLTYPE Save( void) = 0;
        
    };
    
#else 	/* C style interface */

    typedef struct IEventSourceTypeVtbl
    {
        BEGIN_INTERFACE
        
        HRESULT ( STDMETHODCALLTYPE *QueryInterface )( 
            IEventSourceType * This,
            /* [in] */ REFIID riid,
            /* [annotation][iid_is][out] */ 
            __RPC__deref_out  void **ppvObject);
        
        ULONG ( STDMETHODCALLTYPE *AddRef )( 
            IEventSourceType * This);
        
        ULONG ( STDMETHODCALLTYPE *Release )( 
            IEventSourceType * This);
        
        HRESULT ( STDMETHODCALLTYPE *GetTypeInfoCount )( 
            IEventSourceType * This,
            /* [out] */ UINT *pctinfo);
        
        HRESULT ( STDMETHODCALLTYPE *GetTypeInfo )( 
            IEventSourceType * This,
            /* [in] */ UINT iTInfo,
            /* [in] */ LCID lcid,
            /* [out] */ ITypeInfo **ppTInfo);
        
        HRESULT ( STDMETHODCALLTYPE *GetIDsOfNames )( 
            IEventSourceType * This,
            /* [in] */ REFIID riid,
            /* [size_is][in] */ LPOLESTR *rgszNames,
            /* [range][in] */ UINT cNames,
            /* [in] */ LCID lcid,
            /* [size_is][out] */ DISPID *rgDispId);
        
        /* [local] */ HRESULT ( STDMETHODCALLTYPE *Invoke )( 
            IEventSourceType * This,
            /* [in] */ DISPID dispIdMember,
            /* [in] */ REFIID riid,
            /* [in] */ LCID lcid,
            /* [in] */ WORD wFlags,
            /* [out][in] */ DISPPARAMS *pDispParams,
            /* [out] */ VARIANT *pVarResult,
            /* [out] */ EXCEPINFO *pExcepInfo,
            /* [out] */ UINT *puArgErr);
        
        /* [propget][helpstring] */ HRESULT ( STDMETHODCALLTYPE *get_ID )( 
            IEventSourceType * This,
            /* [retval][out] */ BSTR *pstrID);
        
        /* [propget][helpstring] */ HRESULT ( STDMETHODCALLTYPE *get_DisplayName )( 
            IEventSourceType * This,
            /* [retval][out] */ BSTR *pstrDisplayName);
        
        /* [propput][helpstring] */ HRESULT ( STDMETHODCALLTYPE *put_DisplayName )( 
            IEventSourceType * This,
            /* [in] */ BSTR pszDisplayName);
        
        /* [propputref][helpstring] */ HRESULT ( STDMETHODCALLTYPE *putref_DisplayName )( 
            IEventSourceType * This,
            /* [in] */ BSTR *ppszDisplayName);
        
        /* [propget][helpstring] */ HRESULT ( STDMETHODCALLTYPE *get_EventTypes )( 
            IEventSourceType * This,
            /* [retval][out] */ IEventTypes **ppEventTypes);
        
        /* [propget][helpstring] */ HRESULT ( STDMETHODCALLTYPE *get_Sources )( 
            IEventSourceType * This,
            /* [retval][out] */ IEventSources **ppSources);
        
        /* [helpstring] */ HRESULT ( STDMETHODCALLTYPE *Save )( 
            IEventSourceType * This);
        
        END_INTERFACE
    } IEventSourceTypeVtbl;

    interface IEventSourceType
    {
        CONST_VTBL struct IEventSourceTypeVtbl *lpVtbl;
    };

    

#ifdef COBJMACROS


#define IEventSourceType_QueryInterface(This,riid,ppvObject)	\
    ( (This)->lpVtbl -> QueryInterface(This,riid,ppvObject) ) 

#define IEventSourceType_AddRef(This)	\
    ( (This)->lpVtbl -> AddRef(This) ) 

#define IEventSourceType_Release(This)	\
    ( (This)->lpVtbl -> Release(This) ) 


#define IEventSourceType_GetTypeInfoCount(This,pctinfo)	\
    ( (This)->lpVtbl -> GetTypeInfoCount(This,pctinfo) ) 

#define IEventSourceType_GetTypeInfo(This,iTInfo,lcid,ppTInfo)	\
    ( (This)->lpVtbl -> GetTypeInfo(This,iTInfo,lcid,ppTInfo) ) 

#define IEventSourceType_GetIDsOfNames(This,riid,rgszNames,cNames,lcid,rgDispId)	\
    ( (This)->lpVtbl -> GetIDsOfNames(This,riid,rgszNames,cNames,lcid,rgDispId) ) 

#define IEventSourceType_Invoke(This,dispIdMember,riid,lcid,wFlags,pDispParams,pVarResult,pExcepInfo,puArgErr)	\
    ( (This)->lpVtbl -> Invoke(This,dispIdMember,riid,lcid,wFlags,pDispParams,pVarResult,pExcepInfo,puArgErr) ) 


#define IEventSourceType_get_ID(This,pstrID)	\
    ( (This)->lpVtbl -> get_ID(This,pstrID) ) 

#define IEventSourceType_get_DisplayName(This,pstrDisplayName)	\
    ( (This)->lpVtbl -> get_DisplayName(This,pstrDisplayName) ) 

#define IEventSourceType_put_DisplayName(This,pszDisplayName)	\
    ( (This)->lpVtbl -> put_DisplayName(This,pszDisplayName) ) 

#define IEventSourceType_putref_DisplayName(This,ppszDisplayName)	\
    ( (This)->lpVtbl -> putref_DisplayName(This,ppszDisplayName) ) 

#define IEventSourceType_get_EventTypes(This,ppEventTypes)	\
    ( (This)->lpVtbl -> get_EventTypes(This,ppEventTypes) ) 

#define IEventSourceType_get_Sources(This,ppSources)	\
    ( (This)->lpVtbl -> get_Sources(This,ppSources) ) 

#define IEventSourceType_Save(This)	\
    ( (This)->lpVtbl -> Save(This) ) 

#endif /* COBJMACROS */


#endif 	/* C style interface */




#endif 	/* __IEventSourceType_INTERFACE_DEFINED__ */


#ifndef __IEventSourceTypes_INTERFACE_DEFINED__
#define __IEventSourceTypes_INTERFACE_DEFINED__

/* interface IEventSourceTypes */
/* [uuid][unique][oleautomation][object][helpstring][dual] */ 


EXTERN_C const IID IID_IEventSourceTypes;

#if defined(__cplusplus) && !defined(CINTERFACE)
    
    MIDL_INTERFACE("caf30fd0-a651-11d0-a9ea-00aa00685c74")
    IEventSourceTypes : public IDispatch
    {
    public:
        virtual /* [id][helpstring] */ HRESULT STDMETHODCALLTYPE Item( 
            /* [in] */ VARIANT *pvarDesired,
            /* [retval][out] */ IEventSourceType **ppSourceType) = 0;
        
        virtual /* [helpstring] */ HRESULT STDMETHODCALLTYPE Add( 
            /* [in] */ BSTR pszSourceType,
            /* [retval][out] */ IEventSourceType **ppSourceType) = 0;
        
        virtual /* [helpstring] */ HRESULT STDMETHODCALLTYPE Remove( 
            /* [in] */ VARIANT *pvarDesired) = 0;
        
        virtual /* [propget][helpstring] */ HRESULT STDMETHODCALLTYPE get_Count( 
            /* [retval][out] */ long *plCount) = 0;
        
        virtual /* [hidden][propget][id][helpstring] */ HRESULT STDMETHODCALLTYPE get__NewEnum( 
            /* [retval][out] */ IUnknown **ppUnkEnum) = 0;
        
    };
    
#else 	/* C style interface */

    typedef struct IEventSourceTypesVtbl
    {
        BEGIN_INTERFACE
        
        HRESULT ( STDMETHODCALLTYPE *QueryInterface )( 
            IEventSourceTypes * This,
            /* [in] */ REFIID riid,
            /* [annotation][iid_is][out] */ 
            __RPC__deref_out  void **ppvObject);
        
        ULONG ( STDMETHODCALLTYPE *AddRef )( 
            IEventSourceTypes * This);
        
        ULONG ( STDMETHODCALLTYPE *Release )( 
            IEventSourceTypes * This);
        
        HRESULT ( STDMETHODCALLTYPE *GetTypeInfoCount )( 
            IEventSourceTypes * This,
            /* [out] */ UINT *pctinfo);
        
        HRESULT ( STDMETHODCALLTYPE *GetTypeInfo )( 
            IEventSourceTypes * This,
            /* [in] */ UINT iTInfo,
            /* [in] */ LCID lcid,
            /* [out] */ ITypeInfo **ppTInfo);
        
        HRESULT ( STDMETHODCALLTYPE *GetIDsOfNames )( 
            IEventSourceTypes * This,
            /* [in] */ REFIID riid,
            /* [size_is][in] */ LPOLESTR *rgszNames,
            /* [range][in] */ UINT cNames,
            /* [in] */ LCID lcid,
            /* [size_is][out] */ DISPID *rgDispId);
        
        /* [local] */ HRESULT ( STDMETHODCALLTYPE *Invoke )( 
            IEventSourceTypes * This,
            /* [in] */ DISPID dispIdMember,
            /* [in] */ REFIID riid,
            /* [in] */ LCID lcid,
            /* [in] */ WORD wFlags,
            /* [out][in] */ DISPPARAMS *pDispParams,
            /* [out] */ VARIANT *pVarResult,
            /* [out] */ EXCEPINFO *pExcepInfo,
            /* [out] */ UINT *puArgErr);
        
        /* [id][helpstring] */ HRESULT ( STDMETHODCALLTYPE *Item )( 
            IEventSourceTypes * This,
            /* [in] */ VARIANT *pvarDesired,
            /* [retval][out] */ IEventSourceType **ppSourceType);
        
        /* [helpstring] */ HRESULT ( STDMETHODCALLTYPE *Add )( 
            IEventSourceTypes * This,
            /* [in] */ BSTR pszSourceType,
            /* [retval][out] */ IEventSourceType **ppSourceType);
        
        /* [helpstring] */ HRESULT ( STDMETHODCALLTYPE *Remove )( 
            IEventSourceTypes * This,
            /* [in] */ VARIANT *pvarDesired);
        
        /* [propget][helpstring] */ HRESULT ( STDMETHODCALLTYPE *get_Count )( 
            IEventSourceTypes * This,
            /* [retval][out] */ long *plCount);
        
        /* [hidden][propget][id][helpstring] */ HRESULT ( STDMETHODCALLTYPE *get__NewEnum )( 
            IEventSourceTypes * This,
            /* [retval][out] */ IUnknown **ppUnkEnum);
        
        END_INTERFACE
    } IEventSourceTypesVtbl;

    interface IEventSourceTypes
    {
        CONST_VTBL struct IEventSourceTypesVtbl *lpVtbl;
    };

    

#ifdef COBJMACROS


#define IEventSourceTypes_QueryInterface(This,riid,ppvObject)	\
    ( (This)->lpVtbl -> QueryInterface(This,riid,ppvObject) ) 

#define IEventSourceTypes_AddRef(This)	\
    ( (This)->lpVtbl -> AddRef(This) ) 

#define IEventSourceTypes_Release(This)	\
    ( (This)->lpVtbl -> Release(This) ) 


#define IEventSourceTypes_GetTypeInfoCount(This,pctinfo)	\
    ( (This)->lpVtbl -> GetTypeInfoCount(This,pctinfo) ) 

#define IEventSourceTypes_GetTypeInfo(This,iTInfo,lcid,ppTInfo)	\
    ( (This)->lpVtbl -> GetTypeInfo(This,iTInfo,lcid,ppTInfo) ) 

#define IEventSourceTypes_GetIDsOfNames(This,riid,rgszNames,cNames,lcid,rgDispId)	\
    ( (This)->lpVtbl -> GetIDsOfNames(This,riid,rgszNames,cNames,lcid,rgDispId) ) 

#define IEventSourceTypes_Invoke(This,dispIdMember,riid,lcid,wFlags,pDispParams,pVarResult,pExcepInfo,puArgErr)	\
    ( (This)->lpVtbl -> Invoke(This,dispIdMember,riid,lcid,wFlags,pDispParams,pVarResult,pExcepInfo,puArgErr) ) 


#define IEventSourceTypes_Item(This,pvarDesired,ppSourceType)	\
    ( (This)->lpVtbl -> Item(This,pvarDesired,ppSourceType) ) 

#define IEventSourceTypes_Add(This,pszSourceType,ppSourceType)	\
    ( (This)->lpVtbl -> Add(This,pszSourceType,ppSourceType) ) 

#define IEventSourceTypes_Remove(This,pvarDesired)	\
    ( (This)->lpVtbl -> Remove(This,pvarDesired) ) 

#define IEventSourceTypes_get_Count(This,plCount)	\
    ( (This)->lpVtbl -> get_Count(This,plCount) ) 

#define IEventSourceTypes_get__NewEnum(This,ppUnkEnum)	\
    ( (This)->lpVtbl -> get__NewEnum(This,ppUnkEnum) ) 

#endif /* COBJMACROS */


#endif 	/* C style interface */




#endif 	/* __IEventSourceTypes_INTERFACE_DEFINED__ */


#ifndef __IEventManager_INTERFACE_DEFINED__
#define __IEventManager_INTERFACE_DEFINED__

/* interface IEventManager */
/* [uuid][unique][oleautomation][object][helpstring][dual] */ 


EXTERN_C const IID IID_IEventManager;

#if defined(__cplusplus) && !defined(CINTERFACE)
    
    MIDL_INTERFACE("5f6012b0-a651-11d0-a9ea-00aa00685c74")
    IEventManager : public IDispatch
    {
    public:
        virtual /* [propget][helpstring] */ HRESULT STDMETHODCALLTYPE get_SourceTypes( 
            /* [retval][out] */ IEventSourceTypes **ppSourceTypes) = 0;
        
        virtual /* [helpstring] */ HRESULT STDMETHODCALLTYPE CreateSink( 
            /* [unique][in] */ IEventBinding *pBinding,
            /* [unique][in] */ IEventDeliveryOptions *pDeliveryOptions,
            /* [retval][out] */ IUnknown **ppUnkSink) = 0;
        
    };
    
#else 	/* C style interface */

    typedef struct IEventManagerVtbl
    {
        BEGIN_INTERFACE
        
        HRESULT ( STDMETHODCALLTYPE *QueryInterface )( 
            IEventManager * This,
            /* [in] */ REFIID riid,
            /* [annotation][iid_is][out] */ 
            __RPC__deref_out  void **ppvObject);
        
        ULONG ( STDMETHODCALLTYPE *AddRef )( 
            IEventManager * This);
        
        ULONG ( STDMETHODCALLTYPE *Release )( 
            IEventManager * This);
        
        HRESULT ( STDMETHODCALLTYPE *GetTypeInfoCount )( 
            IEventManager * This,
            /* [out] */ UINT *pctinfo);
        
        HRESULT ( STDMETHODCALLTYPE *GetTypeInfo )( 
            IEventManager * This,
            /* [in] */ UINT iTInfo,
            /* [in] */ LCID lcid,
            /* [out] */ ITypeInfo **ppTInfo);
        
        HRESULT ( STDMETHODCALLTYPE *GetIDsOfNames )( 
            IEventManager * This,
            /* [in] */ REFIID riid,
            /* [size_is][in] */ LPOLESTR *rgszNames,
            /* [range][in] */ UINT cNames,
            /* [in] */ LCID lcid,
            /* [size_is][out] */ DISPID *rgDispId);
        
        /* [local] */ HRESULT ( STDMETHODCALLTYPE *Invoke )( 
            IEventManager * This,
            /* [in] */ DISPID dispIdMember,
            /* [in] */ REFIID riid,
            /* [in] */ LCID lcid,
            /* [in] */ WORD wFlags,
            /* [out][in] */ DISPPARAMS *pDispParams,
            /* [out] */ VARIANT *pVarResult,
            /* [out] */ EXCEPINFO *pExcepInfo,
            /* [out] */ UINT *puArgErr);
        
        /* [propget][helpstring] */ HRESULT ( STDMETHODCALLTYPE *get_SourceTypes )( 
            IEventManager * This,
            /* [retval][out] */ IEventSourceTypes **ppSourceTypes);
        
        /* [helpstring] */ HRESULT ( STDMETHODCALLTYPE *CreateSink )( 
            IEventManager * This,
            /* [unique][in] */ IEventBinding *pBinding,
            /* [unique][in] */ IEventDeliveryOptions *pDeliveryOptions,
            /* [retval][out] */ IUnknown **ppUnkSink);
        
        END_INTERFACE
    } IEventManagerVtbl;

    interface IEventManager
    {
        CONST_VTBL struct IEventManagerVtbl *lpVtbl;
    };

    

#ifdef COBJMACROS


#define IEventManager_QueryInterface(This,riid,ppvObject)	\
    ( (This)->lpVtbl -> QueryInterface(This,riid,ppvObject) ) 

#define IEventManager_AddRef(This)	\
    ( (This)->lpVtbl -> AddRef(This) ) 

#define IEventManager_Release(This)	\
    ( (This)->lpVtbl -> Release(This) ) 


#define IEventManager_GetTypeInfoCount(This,pctinfo)	\
    ( (This)->lpVtbl -> GetTypeInfoCount(This,pctinfo) ) 

#define IEventManager_GetTypeInfo(This,iTInfo,lcid,ppTInfo)	\
    ( (This)->lpVtbl -> GetTypeInfo(This,iTInfo,lcid,ppTInfo) ) 

#define IEventManager_GetIDsOfNames(This,riid,rgszNames,cNames,lcid,rgDispId)	\
    ( (This)->lpVtbl -> GetIDsOfNames(This,riid,rgszNames,cNames,lcid,rgDispId) ) 

#define IEventManager_Invoke(This,dispIdMember,riid,lcid,wFlags,pDispParams,pVarResult,pExcepInfo,puArgErr)	\
    ( (This)->lpVtbl -> Invoke(This,dispIdMember,riid,lcid,wFlags,pDispParams,pVarResult,pExcepInfo,puArgErr) ) 


#define IEventManager_get_SourceTypes(This,ppSourceTypes)	\
    ( (This)->lpVtbl -> get_SourceTypes(This,ppSourceTypes) ) 

#define IEventManager_CreateSink(This,pBinding,pDeliveryOptions,ppUnkSink)	\
    ( (This)->lpVtbl -> CreateSink(This,pBinding,pDeliveryOptions,ppUnkSink) ) 

#endif /* COBJMACROS */


#endif 	/* C style interface */




#endif 	/* __IEventManager_INTERFACE_DEFINED__ */


#ifndef __IEventDatabasePlugin_INTERFACE_DEFINED__
#define __IEventDatabasePlugin_INTERFACE_DEFINED__

/* interface IEventDatabasePlugin */
/* [uuid][unique][oleautomation][object][hidden][helpstring] */ 


EXTERN_C const IID IID_IEventDatabasePlugin;

#if defined(__cplusplus) && !defined(CINTERFACE)
    
    MIDL_INTERFACE("4915fb10-af97-11d0-a9eb-00aa00685c74")
    IEventDatabasePlugin : public IUnknown
    {
    public:
        virtual /* [propget][helpstring] */ HRESULT STDMETHODCALLTYPE get_Database( 
            /* [retval][out] */ IEventPropertyBag **ppDatabase) = 0;
        
        virtual /* [propput][helpstring] */ HRESULT STDMETHODCALLTYPE put_Database( 
            /* [in] */ IEventPropertyBag *pDatabase) = 0;
        
        virtual /* [propputref][helpstring] */ HRESULT STDMETHODCALLTYPE putref_Database( 
            /* [in] */ IEventPropertyBag **ppDatabase) = 0;
        
        virtual /* [propget][helpstring] */ HRESULT STDMETHODCALLTYPE get_Name( 
            /* [retval][out] */ BSTR *pstrName) = 0;
        
        virtual /* [propput][helpstring] */ HRESULT STDMETHODCALLTYPE put_Name( 
            /* [in] */ BSTR strName) = 0;
        
        virtual /* [propputref][helpstring] */ HRESULT STDMETHODCALLTYPE putref_Name( 
            /* [in] */ BSTR *pstrName) = 0;
        
        virtual /* [propget][helpstring] */ HRESULT STDMETHODCALLTYPE get_Parent( 
            /* [retval][out] */ IEventPropertyBag **ppParent) = 0;
        
        virtual /* [propput][helpstring] */ HRESULT STDMETHODCALLTYPE put_Parent( 
            /* [in] */ IEventPropertyBag *pParent) = 0;
        
        virtual /* [propputref][helpstring] */ HRESULT STDMETHODCALLTYPE putref_Parent( 
            /* [in] */ IEventPropertyBag **ppParent) = 0;
        
    };
    
#else 	/* C style interface */

    typedef struct IEventDatabasePluginVtbl
    {
        BEGIN_INTERFACE
        
        HRESULT ( STDMETHODCALLTYPE *QueryInterface )( 
            IEventDatabasePlugin * This,
            /* [in] */ REFIID riid,
            /* [annotation][iid_is][out] */ 
            __RPC__deref_out  void **ppvObject);
        
        ULONG ( STDMETHODCALLTYPE *AddRef )( 
            IEventDatabasePlugin * This);
        
        ULONG ( STDMETHODCALLTYPE *Release )( 
            IEventDatabasePlugin * This);
        
        /* [propget][helpstring] */ HRESULT ( STDMETHODCALLTYPE *get_Database )( 
            IEventDatabasePlugin * This,
            /* [retval][out] */ IEventPropertyBag **ppDatabase);
        
        /* [propput][helpstring] */ HRESULT ( STDMETHODCALLTYPE *put_Database )( 
            IEventDatabasePlugin * This,
            /* [in] */ IEventPropertyBag *pDatabase);
        
        /* [propputref][helpstring] */ HRESULT ( STDMETHODCALLTYPE *putref_Database )( 
            IEventDatabasePlugin * This,
            /* [in] */ IEventPropertyBag **ppDatabase);
        
        /* [propget][helpstring] */ HRESULT ( STDMETHODCALLTYPE *get_Name )( 
            IEventDatabasePlugin * This,
            /* [retval][out] */ BSTR *pstrName);
        
        /* [propput][helpstring] */ HRESULT ( STDMETHODCALLTYPE *put_Name )( 
            IEventDatabasePlugin * This,
            /* [in] */ BSTR strName);
        
        /* [propputref][helpstring] */ HRESULT ( STDMETHODCALLTYPE *putref_Name )( 
            IEventDatabasePlugin * This,
            /* [in] */ BSTR *pstrName);
        
        /* [propget][helpstring] */ HRESULT ( STDMETHODCALLTYPE *get_Parent )( 
            IEventDatabasePlugin * This,
            /* [retval][out] */ IEventPropertyBag **ppParent);
        
        /* [propput][helpstring] */ HRESULT ( STDMETHODCALLTYPE *put_Parent )( 
            IEventDatabasePlugin * This,
            /* [in] */ IEventPropertyBag *pParent);
        
        /* [propputref][helpstring] */ HRESULT ( STDMETHODCALLTYPE *putref_Parent )( 
            IEventDatabasePlugin * This,
            /* [in] */ IEventPropertyBag **ppParent);
        
        END_INTERFACE
    } IEventDatabasePluginVtbl;

    interface IEventDatabasePlugin
    {
        CONST_VTBL struct IEventDatabasePluginVtbl *lpVtbl;
    };

    

#ifdef COBJMACROS


#define IEventDatabasePlugin_QueryInterface(This,riid,ppvObject)	\
    ( (This)->lpVtbl -> QueryInterface(This,riid,ppvObject) ) 

#define IEventDatabasePlugin_AddRef(This)	\
    ( (This)->lpVtbl -> AddRef(This) ) 

#define IEventDatabasePlugin_Release(This)	\
    ( (This)->lpVtbl -> Release(This) ) 


#define IEventDatabasePlugin_get_Database(This,ppDatabase)	\
    ( (This)->lpVtbl -> get_Database(This,ppDatabase) ) 

#define IEventDatabasePlugin_put_Database(This,pDatabase)	\
    ( (This)->lpVtbl -> put_Database(This,pDatabase) ) 

#define IEventDatabasePlugin_putref_Database(This,ppDatabase)	\
    ( (This)->lpVtbl -> putref_Database(This,ppDatabase) ) 

#define IEventDatabasePlugin_get_Name(This,pstrName)	\
    ( (This)->lpVtbl -> get_Name(This,pstrName) ) 

#define IEventDatabasePlugin_put_Name(This,strName)	\
    ( (This)->lpVtbl -> put_Name(This,strName) ) 

#define IEventDatabasePlugin_putref_Name(This,pstrName)	\
    ( (This)->lpVtbl -> putref_Name(This,pstrName) ) 

#define IEventDatabasePlugin_get_Parent(This,ppParent)	\
    ( (This)->lpVtbl -> get_Parent(This,ppParent) ) 

#define IEventDatabasePlugin_put_Parent(This,pParent)	\
    ( (This)->lpVtbl -> put_Parent(This,pParent) ) 

#define IEventDatabasePlugin_putref_Parent(This,ppParent)	\
    ( (This)->lpVtbl -> putref_Parent(This,ppParent) ) 

#endif /* COBJMACROS */


#endif 	/* C style interface */




#endif 	/* __IEventDatabasePlugin_INTERFACE_DEFINED__ */


#ifndef __IEventDatabaseManager_INTERFACE_DEFINED__
#define __IEventDatabaseManager_INTERFACE_DEFINED__

/* interface IEventDatabaseManager */
/* [uuid][unique][oleautomation][object][helpstring] */ 


EXTERN_C const IID IID_IEventDatabaseManager;

#if defined(__cplusplus) && !defined(CINTERFACE)
    
    MIDL_INTERFACE("adc25b30-cbd8-11d0-a9f8-00aa00685c74")
    IEventDatabaseManager : public IDispatch
    {
    public:
        virtual /* [helpstring] */ HRESULT STDMETHODCALLTYPE CreateDatabase( 
            /* [in] */ BSTR strPath,
            /* [retval][out] */ IUnknown **ppMonDatabase) = 0;
        
        virtual /* [helpstring] */ HRESULT STDMETHODCALLTYPE EraseDatabase( 
            /* [in] */ BSTR strPath) = 0;
        
        virtual /* [helpstring] */ HRESULT STDMETHODCALLTYPE MakeVServerPath( 
            /* [in] */ BSTR strService,
            /* [in] */ long lInstance,
            /* [retval][out] */ BSTR *pstrPath) = 0;
        
        virtual /* [helpstring] */ HRESULT STDMETHODCALLTYPE MakeVRootPath( 
            /* [in] */ BSTR strService,
            /* [in] */ long lInstance,
            /* [in] */ BSTR strRoot,
            /* [retval][out] */ BSTR *pstrPath) = 0;
        
    };
    
#else 	/* C style interface */

    typedef struct IEventDatabaseManagerVtbl
    {
        BEGIN_INTERFACE
        
        HRESULT ( STDMETHODCALLTYPE *QueryInterface )( 
            IEventDatabaseManager * This,
            /* [in] */ REFIID riid,
            /* [annotation][iid_is][out] */ 
            __RPC__deref_out  void **ppvObject);
        
        ULONG ( STDMETHODCALLTYPE *AddRef )( 
            IEventDatabaseManager * This);
        
        ULONG ( STDMETHODCALLTYPE *Release )( 
            IEventDatabaseManager * This);
        
        HRESULT ( STDMETHODCALLTYPE *GetTypeInfoCount )( 
            IEventDatabaseManager * This,
            /* [out] */ UINT *pctinfo);
        
        HRESULT ( STDMETHODCALLTYPE *GetTypeInfo )( 
            IEventDatabaseManager * This,
            /* [in] */ UINT iTInfo,
            /* [in] */ LCID lcid,
            /* [out] */ ITypeInfo **ppTInfo);
        
        HRESULT ( STDMETHODCALLTYPE *GetIDsOfNames )( 
            IEventDatabaseManager * This,
            /* [in] */ REFIID riid,
            /* [size_is][in] */ LPOLESTR *rgszNames,
            /* [range][in] */ UINT cNames,
            /* [in] */ LCID lcid,
            /* [size_is][out] */ DISPID *rgDispId);
        
        /* [local] */ HRESULT ( STDMETHODCALLTYPE *Invoke )( 
            IEventDatabaseManager * This,
            /* [in] */ DISPID dispIdMember,
            /* [in] */ REFIID riid,
            /* [in] */ LCID lcid,
            /* [in] */ WORD wFlags,
            /* [out][in] */ DISPPARAMS *pDispParams,
            /* [out] */ VARIANT *pVarResult,
            /* [out] */ EXCEPINFO *pExcepInfo,
            /* [out] */ UINT *puArgErr);
        
        /* [helpstring] */ HRESULT ( STDMETHODCALLTYPE *CreateDatabase )( 
            IEventDatabaseManager * This,
            /* [in] */ BSTR strPath,
            /* [retval][out] */ IUnknown **ppMonDatabase);
        
        /* [helpstring] */ HRESULT ( STDMETHODCALLTYPE *EraseDatabase )( 
            IEventDatabaseManager * This,
            /* [in] */ BSTR strPath);
        
        /* [helpstring] */ HRESULT ( STDMETHODCALLTYPE *MakeVServerPath )( 
            IEventDatabaseManager * This,
            /* [in] */ BSTR strService,
            /* [in] */ long lInstance,
            /* [retval][out] */ BSTR *pstrPath);
        
        /* [helpstring] */ HRESULT ( STDMETHODCALLTYPE *MakeVRootPath )( 
            IEventDatabaseManager * This,
            /* [in] */ BSTR strService,
            /* [in] */ long lInstance,
            /* [in] */ BSTR strRoot,
            /* [retval][out] */ BSTR *pstrPath);
        
        END_INTERFACE
    } IEventDatabaseManagerVtbl;

    interface IEventDatabaseManager
    {
        CONST_VTBL struct IEventDatabaseManagerVtbl *lpVtbl;
    };

    

#ifdef COBJMACROS


#define IEventDatabaseManager_QueryInterface(This,riid,ppvObject)	\
    ( (This)->lpVtbl -> QueryInterface(This,riid,ppvObject) ) 

#define IEventDatabaseManager_AddRef(This)	\
    ( (This)->lpVtbl -> AddRef(This) ) 

#define IEventDatabaseManager_Release(This)	\
    ( (This)->lpVtbl -> Release(This) ) 


#define IEventDatabaseManager_GetTypeInfoCount(This,pctinfo)	\
    ( (This)->lpVtbl -> GetTypeInfoCount(This,pctinfo) ) 

#define IEventDatabaseManager_GetTypeInfo(This,iTInfo,lcid,ppTInfo)	\
    ( (This)->lpVtbl -> GetTypeInfo(This,iTInfo,lcid,ppTInfo) ) 

#define IEventDatabaseManager_GetIDsOfNames(This,riid,rgszNames,cNames,lcid,rgDispId)	\
    ( (This)->lpVtbl -> GetIDsOfNames(This,riid,rgszNames,cNames,lcid,rgDispId) ) 

#define IEventDatabaseManager_Invoke(This,dispIdMember,riid,lcid,wFlags,pDispParams,pVarResult,pExcepInfo,puArgErr)	\
    ( (This)->lpVtbl -> Invoke(This,dispIdMember,riid,lcid,wFlags,pDispParams,pVarResult,pExcepInfo,puArgErr) ) 


#define IEventDatabaseManager_CreateDatabase(This,strPath,ppMonDatabase)	\
    ( (This)->lpVtbl -> CreateDatabase(This,strPath,ppMonDatabase) ) 

#define IEventDatabaseManager_EraseDatabase(This,strPath)	\
    ( (This)->lpVtbl -> EraseDatabase(This,strPath) ) 

#define IEventDatabaseManager_MakeVServerPath(This,strService,lInstance,pstrPath)	\
    ( (This)->lpVtbl -> MakeVServerPath(This,strService,lInstance,pstrPath) ) 

#define IEventDatabaseManager_MakeVRootPath(This,strService,lInstance,strRoot,pstrPath)	\
    ( (This)->lpVtbl -> MakeVRootPath(This,strService,lInstance,strRoot,pstrPath) ) 

#endif /* COBJMACROS */


#endif 	/* C style interface */




#endif 	/* __IEventDatabaseManager_INTERFACE_DEFINED__ */


#ifndef __IEventUtil_INTERFACE_DEFINED__
#define __IEventUtil_INTERFACE_DEFINED__

/* interface IEventUtil */
/* [uuid][unique][oleautomation][object][helpstring] */ 


EXTERN_C const IID IID_IEventUtil;

#if defined(__cplusplus) && !defined(CINTERFACE)
    
    MIDL_INTERFACE("c61670e0-cd6e-11d0-a9f8-00aa00685c74")
    IEventUtil : public IDispatch
    {
    public:
        virtual /* [helpstring] */ HRESULT STDMETHODCALLTYPE DisplayNameFromMoniker( 
            /* [in] */ IUnknown *pUnkMoniker,
            /* [retval][out] */ BSTR *pstrDisplayName) = 0;
        
        virtual /* [helpstring] */ HRESULT STDMETHODCALLTYPE MonikerFromDisplayName( 
            /* [in] */ BSTR strDisplayName,
            /* [retval][out] */ IUnknown **ppUnkMoniker) = 0;
        
        virtual /* [helpstring] */ HRESULT STDMETHODCALLTYPE ObjectFromMoniker( 
            /* [in] */ IUnknown *pUnkMoniker,
            /* [retval][out] */ IUnknown **ppUnkObject) = 0;
        
        virtual /* [helpstring] */ HRESULT STDMETHODCALLTYPE GetNewGUID( 
            /* [retval][out] */ BSTR *pstrGUID) = 0;
        
        virtual /* [helpstring] */ HRESULT STDMETHODCALLTYPE CopyPropertyBag( 
            /* [in] */ IUnknown *pUnkInput,
            /* [retval][out] */ IUnknown **ppUnkOutput) = 0;
        
        virtual /* [helpstring] */ HRESULT STDMETHODCALLTYPE CopyPropertyBagShallow( 
            /* [in] */ IUnknown *pUnkInput,
            /* [retval][out] */ IUnknown **ppUnkOutput) = 0;
        
        virtual /* [helpstring] */ HRESULT STDMETHODCALLTYPE DispatchFromObject( 
            /* [in] */ IUnknown *pUnkObject,
            /* [retval][out] */ IDispatch **ppDispOutput) = 0;
        
        virtual /* [helpstring] */ HRESULT STDMETHODCALLTYPE GetIndexedGUID( 
            /* [in] */ BSTR strGUID,
            /* [in] */ long lValue,
            /* [retval][out] */ BSTR *pstrResult) = 0;
        
        virtual /* [helpstring] */ HRESULT STDMETHODCALLTYPE RegisterSource( 
            /* [in] */ BSTR strSourceType,
            /* [in] */ BSTR strSource,
            /* [in] */ long lInstance,
            /* [in] */ BSTR strService,
            /* [in] */ BSTR strVRoot,
            /* [in] */ BSTR strDatabaseManager,
            /* [in] */ BSTR strDisplayName,
            /* [retval][out] */ IEventBindingManager **ppBindingManager) = 0;
        
    };
    
#else 	/* C style interface */

    typedef struct IEventUtilVtbl
    {
        BEGIN_INTERFACE
        
        HRESULT ( STDMETHODCALLTYPE *QueryInterface )( 
            IEventUtil * This,
            /* [in] */ REFIID riid,
            /* [annotation][iid_is][out] */ 
            __RPC__deref_out  void **ppvObject);
        
        ULONG ( STDMETHODCALLTYPE *AddRef )( 
            IEventUtil * This);
        
        ULONG ( STDMETHODCALLTYPE *Release )( 
            IEventUtil * This);
        
        HRESULT ( STDMETHODCALLTYPE *GetTypeInfoCount )( 
            IEventUtil * This,
            /* [out] */ UINT *pctinfo);
        
        HRESULT ( STDMETHODCALLTYPE *GetTypeInfo )( 
            IEventUtil * This,
            /* [in] */ UINT iTInfo,
            /* [in] */ LCID lcid,
            /* [out] */ ITypeInfo **ppTInfo);
        
        HRESULT ( STDMETHODCALLTYPE *GetIDsOfNames )( 
            IEventUtil * This,
            /* [in] */ REFIID riid,
            /* [size_is][in] */ LPOLESTR *rgszNames,
            /* [range][in] */ UINT cNames,
            /* [in] */ LCID lcid,
            /* [size_is][out] */ DISPID *rgDispId);
        
        /* [local] */ HRESULT ( STDMETHODCALLTYPE *Invoke )( 
            IEventUtil * This,
            /* [in] */ DISPID dispIdMember,
            /* [in] */ REFIID riid,
            /* [in] */ LCID lcid,
            /* [in] */ WORD wFlags,
            /* [out][in] */ DISPPARAMS *pDispParams,
            /* [out] */ VARIANT *pVarResult,
            /* [out] */ EXCEPINFO *pExcepInfo,
            /* [out] */ UINT *puArgErr);
        
        /* [helpstring] */ HRESULT ( STDMETHODCALLTYPE *DisplayNameFromMoniker )( 
            IEventUtil * This,
            /* [in] */ IUnknown *pUnkMoniker,
            /* [retval][out] */ BSTR *pstrDisplayName);
        
        /* [helpstring] */ HRESULT ( STDMETHODCALLTYPE *MonikerFromDisplayName )( 
            IEventUtil * This,
            /* [in] */ BSTR strDisplayName,
            /* [retval][out] */ IUnknown **ppUnkMoniker);
        
        /* [helpstring] */ HRESULT ( STDMETHODCALLTYPE *ObjectFromMoniker )( 
            IEventUtil * This,
            /* [in] */ IUnknown *pUnkMoniker,
            /* [retval][out] */ IUnknown **ppUnkObject);
        
        /* [helpstring] */ HRESULT ( STDMETHODCALLTYPE *GetNewGUID )( 
            IEventUtil * This,
            /* [retval][out] */ BSTR *pstrGUID);
        
        /* [helpstring] */ HRESULT ( STDMETHODCALLTYPE *CopyPropertyBag )( 
            IEventUtil * This,
            /* [in] */ IUnknown *pUnkInput,
            /* [retval][out] */ IUnknown **ppUnkOutput);
        
        /* [helpstring] */ HRESULT ( STDMETHODCALLTYPE *CopyPropertyBagShallow )( 
            IEventUtil * This,
            /* [in] */ IUnknown *pUnkInput,
            /* [retval][out] */ IUnknown **ppUnkOutput);
        
        /* [helpstring] */ HRESULT ( STDMETHODCALLTYPE *DispatchFromObject )( 
            IEventUtil * This,
            /* [in] */ IUnknown *pUnkObject,
            /* [retval][out] */ IDispatch **ppDispOutput);
        
        /* [helpstring] */ HRESULT ( STDMETHODCALLTYPE *GetIndexedGUID )( 
            IEventUtil * This,
            /* [in] */ BSTR strGUID,
            /* [in] */ long lValue,
            /* [retval][out] */ BSTR *pstrResult);
        
        /* [helpstring] */ HRESULT ( STDMETHODCALLTYPE *RegisterSource )( 
            IEventUtil * This,
            /* [in] */ BSTR strSourceType,
            /* [in] */ BSTR strSource,
            /* [in] */ long lInstance,
            /* [in] */ BSTR strService,
            /* [in] */ BSTR strVRoot,
            /* [in] */ BSTR strDatabaseManager,
            /* [in] */ BSTR strDisplayName,
            /* [retval][out] */ IEventBindingManager **ppBindingManager);
        
        END_INTERFACE
    } IEventUtilVtbl;

    interface IEventUtil
    {
        CONST_VTBL struct IEventUtilVtbl *lpVtbl;
    };

    

#ifdef COBJMACROS


#define IEventUtil_QueryInterface(This,riid,ppvObject)	\
    ( (This)->lpVtbl -> QueryInterface(This,riid,ppvObject) ) 

#define IEventUtil_AddRef(This)	\
    ( (This)->lpVtbl -> AddRef(This) ) 

#define IEventUtil_Release(This)	\
    ( (This)->lpVtbl -> Release(This) ) 


#define IEventUtil_GetTypeInfoCount(This,pctinfo)	\
    ( (This)->lpVtbl -> GetTypeInfoCount(This,pctinfo) ) 

#define IEventUtil_GetTypeInfo(This,iTInfo,lcid,ppTInfo)	\
    ( (This)->lpVtbl -> GetTypeInfo(This,iTInfo,lcid,ppTInfo) ) 

#define IEventUtil_GetIDsOfNames(This,riid,rgszNames,cNames,lcid,rgDispId)	\
    ( (This)->lpVtbl -> GetIDsOfNames(This,riid,rgszNames,cNames,lcid,rgDispId) ) 

#define IEventUtil_Invoke(This,dispIdMember,riid,lcid,wFlags,pDispParams,pVarResult,pExcepInfo,puArgErr)	\
    ( (This)->lpVtbl -> Invoke(This,dispIdMember,riid,lcid,wFlags,pDispParams,pVarResult,pExcepInfo,puArgErr) ) 


#define IEventUtil_DisplayNameFromMoniker(This,pUnkMoniker,pstrDisplayName)	\
    ( (This)->lpVtbl -> DisplayNameFromMoniker(This,pUnkMoniker,pstrDisplayName) ) 

#define IEventUtil_MonikerFromDisplayName(This,strDisplayName,ppUnkMoniker)	\
    ( (This)->lpVtbl -> MonikerFromDisplayName(This,strDisplayName,ppUnkMoniker) ) 

#define IEventUtil_ObjectFromMoniker(This,pUnkMoniker,ppUnkObject)	\
    ( (This)->lpVtbl -> ObjectFromMoniker(This,pUnkMoniker,ppUnkObject) ) 

#define IEventUtil_GetNewGUID(This,pstrGUID)	\
    ( (This)->lpVtbl -> GetNewGUID(This,pstrGUID) ) 

#define IEventUtil_CopyPropertyBag(This,pUnkInput,ppUnkOutput)	\
    ( (This)->lpVtbl -> CopyPropertyBag(This,pUnkInput,ppUnkOutput) ) 

#define IEventUtil_CopyPropertyBagShallow(This,pUnkInput,ppUnkOutput)	\
    ( (This)->lpVtbl -> CopyPropertyBagShallow(This,pUnkInput,ppUnkOutput) ) 

#define IEventUtil_DispatchFromObject(This,pUnkObject,ppDispOutput)	\
    ( (This)->lpVtbl -> DispatchFromObject(This,pUnkObject,ppDispOutput) ) 

#define IEventUtil_GetIndexedGUID(This,strGUID,lValue,pstrResult)	\
    ( (This)->lpVtbl -> GetIndexedGUID(This,strGUID,lValue,pstrResult) ) 

#define IEventUtil_RegisterSource(This,strSourceType,strSource,lInstance,strService,strVRoot,strDatabaseManager,strDisplayName,ppBindingManager)	\
    ( (This)->lpVtbl -> RegisterSource(This,strSourceType,strSource,lInstance,strService,strVRoot,strDatabaseManager,strDisplayName,ppBindingManager) ) 

#endif /* COBJMACROS */


#endif 	/* C style interface */




#endif 	/* __IEventUtil_INTERFACE_DEFINED__ */


#ifndef __IEventComCat_INTERFACE_DEFINED__
#define __IEventComCat_INTERFACE_DEFINED__

/* interface IEventComCat */
/* [uuid][unique][oleautomation][object][helpstring] */ 


EXTERN_C const IID IID_IEventComCat;

#if defined(__cplusplus) && !defined(CINTERFACE)
    
    MIDL_INTERFACE("65a70ec0-cd87-11d0-a9f8-00aa00685c74")
    IEventComCat : public IDispatch
    {
    public:
        virtual /* [helpstring] */ HRESULT STDMETHODCALLTYPE RegisterCategory( 
            /* [in] */ BSTR pszCategory,
            /* [in] */ BSTR pszDescription,
            /* [in] */ long lcidLanguage) = 0;
        
        virtual /* [helpstring] */ HRESULT STDMETHODCALLTYPE UnRegisterCategory( 
            /* [in] */ BSTR pszCategory) = 0;
        
        virtual /* [helpstring] */ HRESULT STDMETHODCALLTYPE RegisterClassImplementsCategory( 
            /* [in] */ BSTR pszClass,
            /* [in] */ BSTR pszCategory) = 0;
        
        virtual /* [helpstring] */ HRESULT STDMETHODCALLTYPE UnRegisterClassImplementsCategory( 
            /* [in] */ BSTR pszClass,
            /* [in] */ BSTR pszCategory) = 0;
        
        virtual /* [helpstring] */ HRESULT STDMETHODCALLTYPE RegisterClassRequiresCategory( 
            /* [in] */ BSTR pszClass,
            /* [in] */ BSTR pszCategory) = 0;
        
        virtual /* [helpstring] */ HRESULT STDMETHODCALLTYPE UnRegisterClassRequiresCategory( 
            /* [in] */ BSTR pszClass,
            /* [in] */ BSTR pszCategory) = 0;
        
        virtual /* [helpstring] */ HRESULT STDMETHODCALLTYPE GetCategories( 
            /* [retval][out] */ SAFEARRAY * *psaCategories) = 0;
        
        virtual /* [helpstring] */ HRESULT STDMETHODCALLTYPE GetCategoryDescription( 
            /* [in] */ BSTR pszCategory,
            /* [in] */ long lcidLanguage,
            /* [retval][out] */ BSTR *pstrDescription) = 0;
        
    };
    
#else 	/* C style interface */

    typedef struct IEventComCatVtbl
    {
        BEGIN_INTERFACE
        
        HRESULT ( STDMETHODCALLTYPE *QueryInterface )( 
            IEventComCat * This,
            /* [in] */ REFIID riid,
            /* [annotation][iid_is][out] */ 
            __RPC__deref_out  void **ppvObject);
        
        ULONG ( STDMETHODCALLTYPE *AddRef )( 
            IEventComCat * This);
        
        ULONG ( STDMETHODCALLTYPE *Release )( 
            IEventComCat * This);
        
        HRESULT ( STDMETHODCALLTYPE *GetTypeInfoCount )( 
            IEventComCat * This,
            /* [out] */ UINT *pctinfo);
        
        HRESULT ( STDMETHODCALLTYPE *GetTypeInfo )( 
            IEventComCat * This,
            /* [in] */ UINT iTInfo,
            /* [in] */ LCID lcid,
            /* [out] */ ITypeInfo **ppTInfo);
        
        HRESULT ( STDMETHODCALLTYPE *GetIDsOfNames )( 
            IEventComCat * This,
            /* [in] */ REFIID riid,
            /* [size_is][in] */ LPOLESTR *rgszNames,
            /* [range][in] */ UINT cNames,
            /* [in] */ LCID lcid,
            /* [size_is][out] */ DISPID *rgDispId);
        
        /* [local] */ HRESULT ( STDMETHODCALLTYPE *Invoke )( 
            IEventComCat * This,
            /* [in] */ DISPID dispIdMember,
            /* [in] */ REFIID riid,
            /* [in] */ LCID lcid,
            /* [in] */ WORD wFlags,
            /* [out][in] */ DISPPARAMS *pDispParams,
            /* [out] */ VARIANT *pVarResult,
            /* [out] */ EXCEPINFO *pExcepInfo,
            /* [out] */ UINT *puArgErr);
        
        /* [helpstring] */ HRESULT ( STDMETHODCALLTYPE *RegisterCategory )( 
            IEventComCat * This,
            /* [in] */ BSTR pszCategory,
            /* [in] */ BSTR pszDescription,
            /* [in] */ long lcidLanguage);
        
        /* [helpstring] */ HRESULT ( STDMETHODCALLTYPE *UnRegisterCategory )( 
            IEventComCat * This,
            /* [in] */ BSTR pszCategory);
        
        /* [helpstring] */ HRESULT ( STDMETHODCALLTYPE *RegisterClassImplementsCategory )( 
            IEventComCat * This,
            /* [in] */ BSTR pszClass,
            /* [in] */ BSTR pszCategory);
        
        /* [helpstring] */ HRESULT ( STDMETHODCALLTYPE *UnRegisterClassImplementsCategory )( 
            IEventComCat * This,
            /* [in] */ BSTR pszClass,
            /* [in] */ BSTR pszCategory);
        
        /* [helpstring] */ HRESULT ( STDMETHODCALLTYPE *RegisterClassRequiresCategory )( 
            IEventComCat * This,
            /* [in] */ BSTR pszClass,
            /* [in] */ BSTR pszCategory);
        
        /* [helpstring] */ HRESULT ( STDMETHODCALLTYPE *UnRegisterClassRequiresCategory )( 
            IEventComCat * This,
            /* [in] */ BSTR pszClass,
            /* [in] */ BSTR pszCategory);
        
        /* [helpstring] */ HRESULT ( STDMETHODCALLTYPE *GetCategories )( 
            IEventComCat * This,
            /* [retval][out] */ SAFEARRAY * *psaCategories);
        
        /* [helpstring] */ HRESULT ( STDMETHODCALLTYPE *GetCategoryDescription )( 
            IEventComCat * This,
            /* [in] */ BSTR pszCategory,
            /* [in] */ long lcidLanguage,
            /* [retval][out] */ BSTR *pstrDescription);
        
        END_INTERFACE
    } IEventComCatVtbl;

    interface IEventComCat
    {
        CONST_VTBL struct IEventComCatVtbl *lpVtbl;
    };

    

#ifdef COBJMACROS


#define IEventComCat_QueryInterface(This,riid,ppvObject)	\
    ( (This)->lpVtbl -> QueryInterface(This,riid,ppvObject) ) 

#define IEventComCat_AddRef(This)	\
    ( (This)->lpVtbl -> AddRef(This) ) 

#define IEventComCat_Release(This)	\
    ( (This)->lpVtbl -> Release(This) ) 


#define IEventComCat_GetTypeInfoCount(This,pctinfo)	\
    ( (This)->lpVtbl -> GetTypeInfoCount(This,pctinfo) ) 

#define IEventComCat_GetTypeInfo(This,iTInfo,lcid,ppTInfo)	\
    ( (This)->lpVtbl -> GetTypeInfo(This,iTInfo,lcid,ppTInfo) ) 

#define IEventComCat_GetIDsOfNames(This,riid,rgszNames,cNames,lcid,rgDispId)	\
    ( (This)->lpVtbl -> GetIDsOfNames(This,riid,rgszNames,cNames,lcid,rgDispId) ) 

#define IEventComCat_Invoke(This,dispIdMember,riid,lcid,wFlags,pDispParams,pVarResult,pExcepInfo,puArgErr)	\
    ( (This)->lpVtbl -> Invoke(This,dispIdMember,riid,lcid,wFlags,pDispParams,pVarResult,pExcepInfo,puArgErr) ) 


#define IEventComCat_RegisterCategory(This,pszCategory,pszDescription,lcidLanguage)	\
    ( (This)->lpVtbl -> RegisterCategory(This,pszCategory,pszDescription,lcidLanguage) ) 

#define IEventComCat_UnRegisterCategory(This,pszCategory)	\
    ( (This)->lpVtbl -> UnRegisterCategory(This,pszCategory) ) 

#define IEventComCat_RegisterClassImplementsCategory(This,pszClass,pszCategory)	\
    ( (This)->lpVtbl -> RegisterClassImplementsCategory(This,pszClass,pszCategory) ) 

#define IEventComCat_UnRegisterClassImplementsCategory(This,pszClass,pszCategory)	\
    ( (This)->lpVtbl -> UnRegisterClassImplementsCategory(This,pszClass,pszCategory) ) 

#define IEventComCat_RegisterClassRequiresCategory(This,pszClass,pszCategory)	\
    ( (This)->lpVtbl -> RegisterClassRequiresCategory(This,pszClass,pszCategory) ) 

#define IEventComCat_UnRegisterClassRequiresCategory(This,pszClass,pszCategory)	\
    ( (This)->lpVtbl -> UnRegisterClassRequiresCategory(This,pszClass,pszCategory) ) 

#define IEventComCat_GetCategories(This,psaCategories)	\
    ( (This)->lpVtbl -> GetCategories(This,psaCategories) ) 

#define IEventComCat_GetCategoryDescription(This,pszCategory,lcidLanguage,pstrDescription)	\
    ( (This)->lpVtbl -> GetCategoryDescription(This,pszCategory,lcidLanguage,pstrDescription) ) 

#endif /* COBJMACROS */


#endif 	/* C style interface */




#endif 	/* __IEventComCat_INTERFACE_DEFINED__ */


#ifndef __IEventNotifyBindingChange_INTERFACE_DEFINED__
#define __IEventNotifyBindingChange_INTERFACE_DEFINED__

/* interface IEventNotifyBindingChange */
/* [uuid][unique][object][hidden][helpstring] */ 


EXTERN_C const IID IID_IEventNotifyBindingChange;

#if defined(__cplusplus) && !defined(CINTERFACE)
    
    MIDL_INTERFACE("08f4f2a0-dc5b-11d0-aa0f-00aa006bc80b")
    IEventNotifyBindingChange : public IUnknown
    {
    public:
        virtual /* [helpstring] */ HRESULT STDMETHODCALLTYPE OnChange( void) = 0;
        
    };
    
#else 	/* C style interface */

    typedef struct IEventNotifyBindingChangeVtbl
    {
        BEGIN_INTERFACE
        
        HRESULT ( STDMETHODCALLTYPE *QueryInterface )( 
            IEventNotifyBindingChange * This,
            /* [in] */ REFIID riid,
            /* [annotation][iid_is][out] */ 
            __RPC__deref_out  void **ppvObject);
        
        ULONG ( STDMETHODCALLTYPE *AddRef )( 
            IEventNotifyBindingChange * This);
        
        ULONG ( STDMETHODCALLTYPE *Release )( 
            IEventNotifyBindingChange * This);
        
        /* [helpstring] */ HRESULT ( STDMETHODCALLTYPE *OnChange )( 
            IEventNotifyBindingChange * This);
        
        END_INTERFACE
    } IEventNotifyBindingChangeVtbl;

    interface IEventNotifyBindingChange
    {
        CONST_VTBL struct IEventNotifyBindingChangeVtbl *lpVtbl;
    };

    

#ifdef COBJMACROS


#define IEventNotifyBindingChange_QueryInterface(This,riid,ppvObject)	\
    ( (This)->lpVtbl -> QueryInterface(This,riid,ppvObject) ) 

#define IEventNotifyBindingChange_AddRef(This)	\
    ( (This)->lpVtbl -> AddRef(This) ) 

#define IEventNotifyBindingChange_Release(This)	\
    ( (This)->lpVtbl -> Release(This) ) 


#define IEventNotifyBindingChange_OnChange(This)	\
    ( (This)->lpVtbl -> OnChange(This) ) 

#endif /* COBJMACROS */


#endif 	/* C style interface */




#endif 	/* __IEventNotifyBindingChange_INTERFACE_DEFINED__ */


#ifndef __IEventNotifyBindingChangeDisp_INTERFACE_DEFINED__
#define __IEventNotifyBindingChangeDisp_INTERFACE_DEFINED__

/* interface IEventNotifyBindingChangeDisp */
/* [uuid][unique][object][hidden][helpstring][dual] */ 


EXTERN_C const IID IID_IEventNotifyBindingChangeDisp;

#if defined(__cplusplus) && !defined(CINTERFACE)
    
    MIDL_INTERFACE("dc3d83b0-e99f-11d0-aa10-00aa006bc80b")
    IEventNotifyBindingChangeDisp : public IDispatch
    {
    public:
        virtual /* [id][helpstring] */ HRESULT STDMETHODCALLTYPE OnChange( void) = 0;
        
    };
    
#else 	/* C style interface */

    typedef struct IEventNotifyBindingChangeDispVtbl
    {
        BEGIN_INTERFACE
        
        HRESULT ( STDMETHODCALLTYPE *QueryInterface )( 
            IEventNotifyBindingChangeDisp * This,
            /* [in] */ REFIID riid,
            /* [annotation][iid_is][out] */ 
            __RPC__deref_out  void **ppvObject);
        
        ULONG ( STDMETHODCALLTYPE *AddRef )( 
            IEventNotifyBindingChangeDisp * This);
        
        ULONG ( STDMETHODCALLTYPE *Release )( 
            IEventNotifyBindingChangeDisp * This);
        
        HRESULT ( STDMETHODCALLTYPE *GetTypeInfoCount )( 
            IEventNotifyBindingChangeDisp * This,
            /* [out] */ UINT *pctinfo);
        
        HRESULT ( STDMETHODCALLTYPE *GetTypeInfo )( 
            IEventNotifyBindingChangeDisp * This,
            /* [in] */ UINT iTInfo,
            /* [in] */ LCID lcid,
            /* [out] */ ITypeInfo **ppTInfo);
        
        HRESULT ( STDMETHODCALLTYPE *GetIDsOfNames )( 
            IEventNotifyBindingChangeDisp * This,
            /* [in] */ REFIID riid,
            /* [size_is][in] */ LPOLESTR *rgszNames,
            /* [range][in] */ UINT cNames,
            /* [in] */ LCID lcid,
            /* [size_is][out] */ DISPID *rgDispId);
        
        /* [local] */ HRESULT ( STDMETHODCALLTYPE *Invoke )( 
            IEventNotifyBindingChangeDisp * This,
            /* [in] */ DISPID dispIdMember,
            /* [in] */ REFIID riid,
            /* [in] */ LCID lcid,
            /* [in] */ WORD wFlags,
            /* [out][in] */ DISPPARAMS *pDispParams,
            /* [out] */ VARIANT *pVarResult,
            /* [out] */ EXCEPINFO *pExcepInfo,
            /* [out] */ UINT *puArgErr);
        
        /* [id][helpstring] */ HRESULT ( STDMETHODCALLTYPE *OnChange )( 
            IEventNotifyBindingChangeDisp * This);
        
        END_INTERFACE
    } IEventNotifyBindingChangeDispVtbl;

    interface IEventNotifyBindingChangeDisp
    {
        CONST_VTBL struct IEventNotifyBindingChangeDispVtbl *lpVtbl;
    };

    

#ifdef COBJMACROS


#define IEventNotifyBindingChangeDisp_QueryInterface(This,riid,ppvObject)	\
    ( (This)->lpVtbl -> QueryInterface(This,riid,ppvObject) ) 

#define IEventNotifyBindingChangeDisp_AddRef(This)	\
    ( (This)->lpVtbl -> AddRef(This) ) 

#define IEventNotifyBindingChangeDisp_Release(This)	\
    ( (This)->lpVtbl -> Release(This) ) 


#define IEventNotifyBindingChangeDisp_GetTypeInfoCount(This,pctinfo)	\
    ( (This)->lpVtbl -> GetTypeInfoCount(This,pctinfo) ) 

#define IEventNotifyBindingChangeDisp_GetTypeInfo(This,iTInfo,lcid,ppTInfo)	\
    ( (This)->lpVtbl -> GetTypeInfo(This,iTInfo,lcid,ppTInfo) ) 

#define IEventNotifyBindingChangeDisp_GetIDsOfNames(This,riid,rgszNames,cNames,lcid,rgDispId)	\
    ( (This)->lpVtbl -> GetIDsOfNames(This,riid,rgszNames,cNames,lcid,rgDispId) ) 

#define IEventNotifyBindingChangeDisp_Invoke(This,dispIdMember,riid,lcid,wFlags,pDispParams,pVarResult,pExcepInfo,puArgErr)	\
    ( (This)->lpVtbl -> Invoke(This,dispIdMember,riid,lcid,wFlags,pDispParams,pVarResult,pExcepInfo,puArgErr) ) 


#define IEventNotifyBindingChangeDisp_OnChange(This)	\
    ( (This)->lpVtbl -> OnChange(This) ) 

#endif /* COBJMACROS */


#endif 	/* C style interface */




#endif 	/* __IEventNotifyBindingChangeDisp_INTERFACE_DEFINED__ */


#ifndef __ISEOInitObject_INTERFACE_DEFINED__
#define __ISEOInitObject_INTERFACE_DEFINED__

/* interface ISEOInitObject */
/* [uuid][unique][object][hidden][helpstring] */ 


EXTERN_C const IID IID_ISEOInitObject;

#if defined(__cplusplus) && !defined(CINTERFACE)
    
    MIDL_INTERFACE("9bb6aab0-af6d-11d0-8bd2-00c04fd42e37")
    ISEOInitObject : public IPersistPropertyBag
    {
    public:
    };
    
#else 	/* C style interface */

    typedef struct ISEOInitObjectVtbl
    {
        BEGIN_INTERFACE
        
        HRESULT ( STDMETHODCALLTYPE *QueryInterface )( 
            ISEOInitObject * This,
            /* [in] */ REFIID riid,
            /* [annotation][iid_is][out] */ 
            __RPC__deref_out  void **ppvObject);
        
        ULONG ( STDMETHODCALLTYPE *AddRef )( 
            ISEOInitObject * This);
        
        ULONG ( STDMETHODCALLTYPE *Release )( 
            ISEOInitObject * This);
        
        HRESULT ( STDMETHODCALLTYPE *GetClassID )( 
            ISEOInitObject * This,
            /* [out] */ CLSID *pClassID);
        
        HRESULT ( STDMETHODCALLTYPE *InitNew )( 
            ISEOInitObject * This);
        
        HRESULT ( STDMETHODCALLTYPE *Load )( 
            ISEOInitObject * This,
            /* [in] */ IPropertyBag *pPropBag,
            /* [unique][in] */ IErrorLog *pErrorLog);
        
        HRESULT ( STDMETHODCALLTYPE *Save )( 
            ISEOInitObject * This,
            /* [in] */ IPropertyBag *pPropBag,
            /* [in] */ BOOL fClearDirty,
            /* [in] */ BOOL fSaveAllProperties);
        
        END_INTERFACE
    } ISEOInitObjectVtbl;

    interface ISEOInitObject
    {
        CONST_VTBL struct ISEOInitObjectVtbl *lpVtbl;
    };

    

#ifdef COBJMACROS


#define ISEOInitObject_QueryInterface(This,riid,ppvObject)	\
    ( (This)->lpVtbl -> QueryInterface(This,riid,ppvObject) ) 

#define ISEOInitObject_AddRef(This)	\
    ( (This)->lpVtbl -> AddRef(This) ) 

#define ISEOInitObject_Release(This)	\
    ( (This)->lpVtbl -> Release(This) ) 


#define ISEOInitObject_GetClassID(This,pClassID)	\
    ( (This)->lpVtbl -> GetClassID(This,pClassID) ) 


#define ISEOInitObject_InitNew(This)	\
    ( (This)->lpVtbl -> InitNew(This) ) 

#define ISEOInitObject_Load(This,pPropBag,pErrorLog)	\
    ( (This)->lpVtbl -> Load(This,pPropBag,pErrorLog) ) 

#define ISEOInitObject_Save(This,pPropBag,fClearDirty,fSaveAllProperties)	\
    ( (This)->lpVtbl -> Save(This,pPropBag,fClearDirty,fSaveAllProperties) ) 


#endif /* COBJMACROS */


#endif 	/* C style interface */




#endif 	/* __ISEOInitObject_INTERFACE_DEFINED__ */


#ifndef __IEventRuleEngine_INTERFACE_DEFINED__
#define __IEventRuleEngine_INTERFACE_DEFINED__

/* interface IEventRuleEngine */
/* [uuid][unique][object][helpstring] */ 


EXTERN_C const IID IID_IEventRuleEngine;

#if defined(__cplusplus) && !defined(CINTERFACE)
    
    MIDL_INTERFACE("da816090-f343-11d0-aa14-00aa006bc80b")
    IEventRuleEngine : public IUnknown
    {
    public:
        virtual /* [helpstring] */ HRESULT STDMETHODCALLTYPE Evaluate( 
            /* [unique][in] */ IUnknown *pEvent) = 0;
        
    };
    
#else 	/* C style interface */

    typedef struct IEventRuleEngineVtbl
    {
        BEGIN_INTERFACE
        
        HRESULT ( STDMETHODCALLTYPE *QueryInterface )( 
            IEventRuleEngine * This,
            /* [in] */ REFIID riid,
            /* [annotation][iid_is][out] */ 
            __RPC__deref_out  void **ppvObject);
        
        ULONG ( STDMETHODCALLTYPE *AddRef )( 
            IEventRuleEngine * This);
        
        ULONG ( STDMETHODCALLTYPE *Release )( 
            IEventRuleEngine * This);
        
        /* [helpstring] */ HRESULT ( STDMETHODCALLTYPE *Evaluate )( 
            IEventRuleEngine * This,
            /* [unique][in] */ IUnknown *pEvent);
        
        END_INTERFACE
    } IEventRuleEngineVtbl;

    interface IEventRuleEngine
    {
        CONST_VTBL struct IEventRuleEngineVtbl *lpVtbl;
    };

    

#ifdef COBJMACROS


#define IEventRuleEngine_QueryInterface(This,riid,ppvObject)	\
    ( (This)->lpVtbl -> QueryInterface(This,riid,ppvObject) ) 

#define IEventRuleEngine_AddRef(This)	\
    ( (This)->lpVtbl -> AddRef(This) ) 

#define IEventRuleEngine_Release(This)	\
    ( (This)->lpVtbl -> Release(This) ) 


#define IEventRuleEngine_Evaluate(This,pEvent)	\
    ( (This)->lpVtbl -> Evaluate(This,pEvent) ) 

#endif /* COBJMACROS */


#endif 	/* C style interface */




#endif 	/* __IEventRuleEngine_INTERFACE_DEFINED__ */


#ifndef __IEventPersistBinding_INTERFACE_DEFINED__
#define __IEventPersistBinding_INTERFACE_DEFINED__

/* interface IEventPersistBinding */
/* [uuid][unique][object][helpstring] */ 


EXTERN_C const IID IID_IEventPersistBinding;

#if defined(__cplusplus) && !defined(CINTERFACE)
    
    MIDL_INTERFACE("e9311660-1a98-11d1-aa26-00aa006bc80b")
    IEventPersistBinding : public IPersist
    {
    public:
        virtual /* [helpstring] */ HRESULT STDMETHODCALLTYPE IsDirty( void) = 0;
        
        virtual /* [helpstring] */ HRESULT STDMETHODCALLTYPE Load( 
            /* [in] */ IEventBinding *piBinding) = 0;
        
        virtual /* [helpstring] */ HRESULT STDMETHODCALLTYPE Save( 
            /* [in] */ IEventBinding *piBinding,
            /* [in] */ VARIANT_BOOL fClearDirty) = 0;
        
    };
    
#else 	/* C style interface */

    typedef struct IEventPersistBindingVtbl
    {
        BEGIN_INTERFACE
        
        HRESULT ( STDMETHODCALLTYPE *QueryInterface )( 
            IEventPersistBinding * This,
            /* [in] */ REFIID riid,
            /* [annotation][iid_is][out] */ 
            __RPC__deref_out  void **ppvObject);
        
        ULONG ( STDMETHODCALLTYPE *AddRef )( 
            IEventPersistBinding * This);
        
        ULONG ( STDMETHODCALLTYPE *Release )( 
            IEventPersistBinding * This);
        
        HRESULT ( STDMETHODCALLTYPE *GetClassID )( 
            IEventPersistBinding * This,
            /* [out] */ CLSID *pClassID);
        
        /* [helpstring] */ HRESULT ( STDMETHODCALLTYPE *IsDirty )( 
            IEventPersistBinding * This);
        
        /* [helpstring] */ HRESULT ( STDMETHODCALLTYPE *Load )( 
            IEventPersistBinding * This,
            /* [in] */ IEventBinding *piBinding);
        
        /* [helpstring] */ HRESULT ( STDMETHODCALLTYPE *Save )( 
            IEventPersistBinding * This,
            /* [in] */ IEventBinding *piBinding,
            /* [in] */ VARIANT_BOOL fClearDirty);
        
        END_INTERFACE
    } IEventPersistBindingVtbl;

    interface IEventPersistBinding
    {
        CONST_VTBL struct IEventPersistBindingVtbl *lpVtbl;
    };

    

#ifdef COBJMACROS


#define IEventPersistBinding_QueryInterface(This,riid,ppvObject)	\
    ( (This)->lpVtbl -> QueryInterface(This,riid,ppvObject) ) 

#define IEventPersistBinding_AddRef(This)	\
    ( (This)->lpVtbl -> AddRef(This) ) 

#define IEventPersistBinding_Release(This)	\
    ( (This)->lpVtbl -> Release(This) ) 


#define IEventPersistBinding_GetClassID(This,pClassID)	\
    ( (This)->lpVtbl -> GetClassID(This,pClassID) ) 


#define IEventPersistBinding_IsDirty(This)	\
    ( (This)->lpVtbl -> IsDirty(This) ) 

#define IEventPersistBinding_Load(This,piBinding)	\
    ( (This)->lpVtbl -> Load(This,piBinding) ) 

#define IEventPersistBinding_Save(This,piBinding,fClearDirty)	\
    ( (This)->lpVtbl -> Save(This,piBinding,fClearDirty) ) 

#endif /* COBJMACROS */


#endif 	/* C style interface */




#endif 	/* __IEventPersistBinding_INTERFACE_DEFINED__ */


#ifndef __IEventSinkNotify_INTERFACE_DEFINED__
#define __IEventSinkNotify_INTERFACE_DEFINED__

/* interface IEventSinkNotify */
/* [uuid][unique][object][helpstring] */ 


EXTERN_C const IID IID_IEventSinkNotify;

#if defined(__cplusplus) && !defined(CINTERFACE)
    
    MIDL_INTERFACE("bdf065b0-f346-11d0-aa14-00aa006bc80b")
    IEventSinkNotify : public IUnknown
    {
    public:
        virtual /* [id][helpstring] */ HRESULT STDMETHODCALLTYPE OnEvent( void) = 0;
        
    };
    
#else 	/* C style interface */

    typedef struct IEventSinkNotifyVtbl
    {
        BEGIN_INTERFACE
        
        HRESULT ( STDMETHODCALLTYPE *QueryInterface )( 
            IEventSinkNotify * This,
            /* [in] */ REFIID riid,
            /* [annotation][iid_is][out] */ 
            __RPC__deref_out  void **ppvObject);
        
        ULONG ( STDMETHODCALLTYPE *AddRef )( 
            IEventSinkNotify * This);
        
        ULONG ( STDMETHODCALLTYPE *Release )( 
            IEventSinkNotify * This);
        
        /* [id][helpstring] */ HRESULT ( STDMETHODCALLTYPE *OnEvent )( 
            IEventSinkNotify * This);
        
        END_INTERFACE
    } IEventSinkNotifyVtbl;

    interface IEventSinkNotify
    {
        CONST_VTBL struct IEventSinkNotifyVtbl *lpVtbl;
    };

    

#ifdef COBJMACROS


#define IEventSinkNotify_QueryInterface(This,riid,ppvObject)	\
    ( (This)->lpVtbl -> QueryInterface(This,riid,ppvObject) ) 

#define IEventSinkNotify_AddRef(This)	\
    ( (This)->lpVtbl -> AddRef(This) ) 

#define IEventSinkNotify_Release(This)	\
    ( (This)->lpVtbl -> Release(This) ) 


#define IEventSinkNotify_OnEvent(This)	\
    ( (This)->lpVtbl -> OnEvent(This) ) 

#endif /* COBJMACROS */


#endif 	/* C style interface */




#endif 	/* __IEventSinkNotify_INTERFACE_DEFINED__ */


#ifndef __IEventSinkNotifyDisp_INTERFACE_DEFINED__
#define __IEventSinkNotifyDisp_INTERFACE_DEFINED__

/* interface IEventSinkNotifyDisp */
/* [uuid][unique][object][helpstring] */ 


EXTERN_C const IID IID_IEventSinkNotifyDisp;

#if defined(__cplusplus) && !defined(CINTERFACE)
    
    MIDL_INTERFACE("33a79660-f347-11d0-aa14-00aa006bc80b")
    IEventSinkNotifyDisp : public IDispatch
    {
    public:
        virtual /* [id][helpstring] */ HRESULT STDMETHODCALLTYPE OnEvent( void) = 0;
        
    };
    
#else 	/* C style interface */

    typedef struct IEventSinkNotifyDispVtbl
    {
        BEGIN_INTERFACE
        
        HRESULT ( STDMETHODCALLTYPE *QueryInterface )( 
            IEventSinkNotifyDisp * This,
            /* [in] */ REFIID riid,
            /* [annotation][iid_is][out] */ 
            __RPC__deref_out  void **ppvObject);
        
        ULONG ( STDMETHODCALLTYPE *AddRef )( 
            IEventSinkNotifyDisp * This);
        
        ULONG ( STDMETHODCALLTYPE *Release )( 
            IEventSinkNotifyDisp * This);
        
        HRESULT ( STDMETHODCALLTYPE *GetTypeInfoCount )( 
            IEventSinkNotifyDisp * This,
            /* [out] */ UINT *pctinfo);
        
        HRESULT ( STDMETHODCALLTYPE *GetTypeInfo )( 
            IEventSinkNotifyDisp * This,
            /* [in] */ UINT iTInfo,
            /* [in] */ LCID lcid,
            /* [out] */ ITypeInfo **ppTInfo);
        
        HRESULT ( STDMETHODCALLTYPE *GetIDsOfNames )( 
            IEventSinkNotifyDisp * This,
            /* [in] */ REFIID riid,
            /* [size_is][in] */ LPOLESTR *rgszNames,
            /* [range][in] */ UINT cNames,
            /* [in] */ LCID lcid,
            /* [size_is][out] */ DISPID *rgDispId);
        
        /* [local] */ HRESULT ( STDMETHODCALLTYPE *Invoke )( 
            IEventSinkNotifyDisp * This,
            /* [in] */ DISPID dispIdMember,
            /* [in] */ REFIID riid,
            /* [in] */ LCID lcid,
            /* [in] */ WORD wFlags,
            /* [out][in] */ DISPPARAMS *pDispParams,
            /* [out] */ VARIANT *pVarResult,
            /* [out] */ EXCEPINFO *pExcepInfo,
            /* [out] */ UINT *puArgErr);
        
        /* [id][helpstring] */ HRESULT ( STDMETHODCALLTYPE *OnEvent )( 
            IEventSinkNotifyDisp * This);
        
        END_INTERFACE
    } IEventSinkNotifyDispVtbl;

    interface IEventSinkNotifyDisp
    {
        CONST_VTBL struct IEventSinkNotifyDispVtbl *lpVtbl;
    };

    

#ifdef COBJMACROS


#define IEventSinkNotifyDisp_QueryInterface(This,riid,ppvObject)	\
    ( (This)->lpVtbl -> QueryInterface(This,riid,ppvObject) ) 

#define IEventSinkNotifyDisp_AddRef(This)	\
    ( (This)->lpVtbl -> AddRef(This) ) 

#define IEventSinkNotifyDisp_Release(This)	\
    ( (This)->lpVtbl -> Release(This) ) 


#define IEventSinkNotifyDisp_GetTypeInfoCount(This,pctinfo)	\
    ( (This)->lpVtbl -> GetTypeInfoCount(This,pctinfo) ) 

#define IEventSinkNotifyDisp_GetTypeInfo(This,iTInfo,lcid,ppTInfo)	\
    ( (This)->lpVtbl -> GetTypeInfo(This,iTInfo,lcid,ppTInfo) ) 

#define IEventSinkNotifyDisp_GetIDsOfNames(This,riid,rgszNames,cNames,lcid,rgDispId)	\
    ( (This)->lpVtbl -> GetIDsOfNames(This,riid,rgszNames,cNames,lcid,rgDispId) ) 

#define IEventSinkNotifyDisp_Invoke(This,dispIdMember,riid,lcid,wFlags,pDispParams,pVarResult,pExcepInfo,puArgErr)	\
    ( (This)->lpVtbl -> Invoke(This,dispIdMember,riid,lcid,wFlags,pDispParams,pVarResult,pExcepInfo,puArgErr) ) 


#define IEventSinkNotifyDisp_OnEvent(This)	\
    ( (This)->lpVtbl -> OnEvent(This) ) 

#endif /* COBJMACROS */


#endif 	/* C style interface */




#endif 	/* __IEventSinkNotifyDisp_INTERFACE_DEFINED__ */


#ifndef __IEventIsCacheable_INTERFACE_DEFINED__
#define __IEventIsCacheable_INTERFACE_DEFINED__

/* interface IEventIsCacheable */
/* [uuid][unique][object][helpstring] */ 


EXTERN_C const IID IID_IEventIsCacheable;

#if defined(__cplusplus) && !defined(CINTERFACE)
    
    MIDL_INTERFACE("22e0f830-1e81-11d1-aa29-00aa006bc80b")
    IEventIsCacheable : public IUnknown
    {
    public:
        virtual /* [helpstring] */ HRESULT STDMETHODCALLTYPE IsCacheable( void) = 0;
        
    };
    
#else 	/* C style interface */

    typedef struct IEventIsCacheableVtbl
    {
        BEGIN_INTERFACE
        
        HRESULT ( STDMETHODCALLTYPE *QueryInterface )( 
            IEventIsCacheable * This,
            /* [in] */ REFIID riid,
            /* [annotation][iid_is][out] */ 
            __RPC__deref_out  void **ppvObject);
        
        ULONG ( STDMETHODCALLTYPE *AddRef )( 
            IEventIsCacheable * This);
        
        ULONG ( STDMETHODCALLTYPE *Release )( 
            IEventIsCacheable * This);
        
        /* [helpstring] */ HRESULT ( STDMETHODCALLTYPE *IsCacheable )( 
            IEventIsCacheable * This);
        
        END_INTERFACE
    } IEventIsCacheableVtbl;

    interface IEventIsCacheable
    {
        CONST_VTBL struct IEventIsCacheableVtbl *lpVtbl;
    };

    

#ifdef COBJMACROS


#define IEventIsCacheable_QueryInterface(This,riid,ppvObject)	\
    ( (This)->lpVtbl -> QueryInterface(This,riid,ppvObject) ) 

#define IEventIsCacheable_AddRef(This)	\
    ( (This)->lpVtbl -> AddRef(This) ) 

#define IEventIsCacheable_Release(This)	\
    ( (This)->lpVtbl -> Release(This) ) 


#define IEventIsCacheable_IsCacheable(This)	\
    ( (This)->lpVtbl -> IsCacheable(This) ) 

#endif /* COBJMACROS */


#endif 	/* C style interface */




#endif 	/* __IEventIsCacheable_INTERFACE_DEFINED__ */


#ifndef __IEventCreateOptions_INTERFACE_DEFINED__
#define __IEventCreateOptions_INTERFACE_DEFINED__

/* interface IEventCreateOptions */
/* [uuid][unique][object][hidden][helpstring] */ 


EXTERN_C const IID IID_IEventCreateOptions;

#if defined(__cplusplus) && !defined(CINTERFACE)
    
    MIDL_INTERFACE("c0287bfe-ef7f-11d1-9fff-00c04fa37348")
    IEventCreateOptions : public IEventDeliveryOptions
    {
    public:
        virtual /* [helpstring] */ HRESULT STDMETHODCALLTYPE CreateBindCtx( 
            /* [in] */ DWORD dwReserved,
            /* [out] */ IBindCtx **ppBindCtx) = 0;
        
        virtual /* [helpstring] */ HRESULT STDMETHODCALLTYPE MkParseDisplayName( 
            /* [in] */ IBindCtx *pBindCtx,
            /* [in] */ LPCOLESTR pszUserName,
            /* [out] */ ULONG *pchEaten,
            /* [out] */ LPMONIKER *ppMoniker) = 0;
        
        virtual /* [helpstring] */ HRESULT STDMETHODCALLTYPE BindToObject( 
            /* [in] */ IMoniker *pMoniker,
            /* [in] */ IBindCtx *pBindCtx,
            /* [in] */ IMoniker *pmkLeft,
            /* [in] */ REFIID riidResult,
            /* [iid_is][out] */ LPVOID *ppvResult) = 0;
        
        virtual /* [helpstring] */ HRESULT STDMETHODCALLTYPE CoCreateInstance( 
            /* [in] */ REFCLSID rclsidDesired,
            /* [in] */ IUnknown *pUnkOuter,
            /* [in] */ DWORD dwClsCtx,
            /* [in] */ REFIID riidResult,
            /* [iid_is][out] */ LPVOID *ppvResult) = 0;
        
        virtual /* [helpstring] */ HRESULT STDMETHODCALLTYPE Init( 
            /* [in] */ REFIID riidObject,
            /* [iid_is][out][in] */ IUnknown **ppUnkObject,
            /* [unique][in] */ IEventBinding *pBinding,
            /* [unique][in] */ IUnknown *pInitProps) = 0;
        
    };
    
#else 	/* C style interface */

    typedef struct IEventCreateOptionsVtbl
    {
        BEGIN_INTERFACE
        
        HRESULT ( STDMETHODCALLTYPE *QueryInterface )( 
            IEventCreateOptions * This,
            /* [in] */ REFIID riid,
            /* [annotation][iid_is][out] */ 
            __RPC__deref_out  void **ppvObject);
        
        ULONG ( STDMETHODCALLTYPE *AddRef )( 
            IEventCreateOptions * This);
        
        ULONG ( STDMETHODCALLTYPE *Release )( 
            IEventCreateOptions * This);
        
        HRESULT ( STDMETHODCALLTYPE *GetTypeInfoCount )( 
            IEventCreateOptions * This,
            /* [out] */ UINT *pctinfo);
        
        HRESULT ( STDMETHODCALLTYPE *GetTypeInfo )( 
            IEventCreateOptions * This,
            /* [in] */ UINT iTInfo,
            /* [in] */ LCID lcid,
            /* [out] */ ITypeInfo **ppTInfo);
        
        HRESULT ( STDMETHODCALLTYPE *GetIDsOfNames )( 
            IEventCreateOptions * This,
            /* [in] */ REFIID riid,
            /* [size_is][in] */ LPOLESTR *rgszNames,
            /* [range][in] */ UINT cNames,
            /* [in] */ LCID lcid,
            /* [size_is][out] */ DISPID *rgDispId);
        
        /* [local] */ HRESULT ( STDMETHODCALLTYPE *Invoke )( 
            IEventCreateOptions * This,
            /* [in] */ DISPID dispIdMember,
            /* [in] */ REFIID riid,
            /* [in] */ LCID lcid,
            /* [in] */ WORD wFlags,
            /* [out][in] */ DISPPARAMS *pDispParams,
            /* [out] */ VARIANT *pVarResult,
            /* [out] */ EXCEPINFO *pExcepInfo,
            /* [out] */ UINT *puArgErr);
        
        /* [helpstring] */ HRESULT ( STDMETHODCALLTYPE *CreateBindCtx )( 
            IEventCreateOptions * This,
            /* [in] */ DWORD dwReserved,
            /* [out] */ IBindCtx **ppBindCtx);
        
        /* [helpstring] */ HRESULT ( STDMETHODCALLTYPE *MkParseDisplayName )( 
            IEventCreateOptions * This,
            /* [in] */ IBindCtx *pBindCtx,
            /* [in] */ LPCOLESTR pszUserName,
            /* [out] */ ULONG *pchEaten,
            /* [out] */ LPMONIKER *ppMoniker);
        
        /* [helpstring] */ HRESULT ( STDMETHODCALLTYPE *BindToObject )( 
            IEventCreateOptions * This,
            /* [in] */ IMoniker *pMoniker,
            /* [in] */ IBindCtx *pBindCtx,
            /* [in] */ IMoniker *pmkLeft,
            /* [in] */ REFIID riidResult,
            /* [iid_is][out] */ LPVOID *ppvResult);
        
        /* [helpstring] */ HRESULT ( STDMETHODCALLTYPE *CoCreateInstance )( 
            IEventCreateOptions * This,
            /* [in] */ REFCLSID rclsidDesired,
            /* [in] */ IUnknown *pUnkOuter,
            /* [in] */ DWORD dwClsCtx,
            /* [in] */ REFIID riidResult,
            /* [iid_is][out] */ LPVOID *ppvResult);
        
        /* [helpstring] */ HRESULT ( STDMETHODCALLTYPE *Init )( 
            IEventCreateOptions * This,
            /* [in] */ REFIID riidObject,
            /* [iid_is][out][in] */ IUnknown **ppUnkObject,
            /* [unique][in] */ IEventBinding *pBinding,
            /* [unique][in] */ IUnknown *pInitProps);
        
        END_INTERFACE
    } IEventCreateOptionsVtbl;

    interface IEventCreateOptions
    {
        CONST_VTBL struct IEventCreateOptionsVtbl *lpVtbl;
    };

    

#ifdef COBJMACROS


#define IEventCreateOptions_QueryInterface(This,riid,ppvObject)	\
    ( (This)->lpVtbl -> QueryInterface(This,riid,ppvObject) ) 

#define IEventCreateOptions_AddRef(This)	\
    ( (This)->lpVtbl -> AddRef(This) ) 

#define IEventCreateOptions_Release(This)	\
    ( (This)->lpVtbl -> Release(This) ) 


#define IEventCreateOptions_GetTypeInfoCount(This,pctinfo)	\
    ( (This)->lpVtbl -> GetTypeInfoCount(This,pctinfo) ) 

#define IEventCreateOptions_GetTypeInfo(This,iTInfo,lcid,ppTInfo)	\
    ( (This)->lpVtbl -> GetTypeInfo(This,iTInfo,lcid,ppTInfo) ) 

#define IEventCreateOptions_GetIDsOfNames(This,riid,rgszNames,cNames,lcid,rgDispId)	\
    ( (This)->lpVtbl -> GetIDsOfNames(This,riid,rgszNames,cNames,lcid,rgDispId) ) 

#define IEventCreateOptions_Invoke(This,dispIdMember,riid,lcid,wFlags,pDispParams,pVarResult,pExcepInfo,puArgErr)	\
    ( (This)->lpVtbl -> Invoke(This,dispIdMember,riid,lcid,wFlags,pDispParams,pVarResult,pExcepInfo,puArgErr) ) 



#define IEventCreateOptions_CreateBindCtx(This,dwReserved,ppBindCtx)	\
    ( (This)->lpVtbl -> CreateBindCtx(This,dwReserved,ppBindCtx) ) 

#define IEventCreateOptions_MkParseDisplayName(This,pBindCtx,pszUserName,pchEaten,ppMoniker)	\
    ( (This)->lpVtbl -> MkParseDisplayName(This,pBindCtx,pszUserName,pchEaten,ppMoniker) ) 

#define IEventCreateOptions_BindToObject(This,pMoniker,pBindCtx,pmkLeft,riidResult,ppvResult)	\
    ( (This)->lpVtbl -> BindToObject(This,pMoniker,pBindCtx,pmkLeft,riidResult,ppvResult) ) 

#define IEventCreateOptions_CoCreateInstance(This,rclsidDesired,pUnkOuter,dwClsCtx,riidResult,ppvResult)	\
    ( (This)->lpVtbl -> CoCreateInstance(This,rclsidDesired,pUnkOuter,dwClsCtx,riidResult,ppvResult) ) 

#define IEventCreateOptions_Init(This,riidObject,ppUnkObject,pBinding,pInitProps)	\
    ( (This)->lpVtbl -> Init(This,riidObject,ppUnkObject,pBinding,pInitProps) ) 

#endif /* COBJMACROS */


#endif 	/* C style interface */




#endif 	/* __IEventCreateOptions_INTERFACE_DEFINED__ */


#ifndef __IEventDispatcherChain_INTERFACE_DEFINED__
#define __IEventDispatcherChain_INTERFACE_DEFINED__

/* interface IEventDispatcherChain */
/* [uuid][unique][object][hidden][helpstring] */ 


EXTERN_C const IID IID_IEventDispatcherChain;

#if defined(__cplusplus) && !defined(CINTERFACE)
    
    MIDL_INTERFACE("58a90754-fb15-11d1-a00c-00c04fa37348")
    IEventDispatcherChain : public IUnknown
    {
    public:
        virtual /* [helpstring] */ HRESULT STDMETHODCALLTYPE SetPrevious( 
            /* [in] */ IUnknown *pUnkPrevious,
            /* [out] */ IUnknown **ppUnkPreload) = 0;
        
    };
    
#else 	/* C style interface */

    typedef struct IEventDispatcherChainVtbl
    {
        BEGIN_INTERFACE
        
        HRESULT ( STDMETHODCALLTYPE *QueryInterface )( 
            IEventDispatcherChain * This,
            /* [in] */ REFIID riid,
            /* [annotation][iid_is][out] */ 
            __RPC__deref_out  void **ppvObject);
        
        ULONG ( STDMETHODCALLTYPE *AddRef )( 
            IEventDispatcherChain * This);
        
        ULONG ( STDMETHODCALLTYPE *Release )( 
            IEventDispatcherChain * This);
        
        /* [helpstring] */ HRESULT ( STDMETHODCALLTYPE *SetPrevious )( 
            IEventDispatcherChain * This,
            /* [in] */ IUnknown *pUnkPrevious,
            /* [out] */ IUnknown **ppUnkPreload);
        
        END_INTERFACE
    } IEventDispatcherChainVtbl;

    interface IEventDispatcherChain
    {
        CONST_VTBL struct IEventDispatcherChainVtbl *lpVtbl;
    };

    

#ifdef COBJMACROS


#define IEventDispatcherChain_QueryInterface(This,riid,ppvObject)	\
    ( (This)->lpVtbl -> QueryInterface(This,riid,ppvObject) ) 

#define IEventDispatcherChain_AddRef(This)	\
    ( (This)->lpVtbl -> AddRef(This) ) 

#define IEventDispatcherChain_Release(This)	\
    ( (This)->lpVtbl -> Release(This) ) 


#define IEventDispatcherChain_SetPrevious(This,pUnkPrevious,ppUnkPreload)	\
    ( (This)->lpVtbl -> SetPrevious(This,pUnkPrevious,ppUnkPreload) ) 

#endif /* COBJMACROS */


#endif 	/* C style interface */




#endif 	/* __IEventDispatcherChain_INTERFACE_DEFINED__ */



#ifndef __SEOLib_LIBRARY_DEFINED__
#define __SEOLib_LIBRARY_DEFINED__

/* library SEOLib */
/* [version][uuid][helpstring] */ 








































#define	SEO_S_MOREDATA	( 0x41001 )

#define	SEO_E_NOTPRESENT	( 0x80041002 )

#define	SEO_E_TIMEOUT	( 0x80041003 )

#define	SEO_S_DONEPROCESSING	( 0x80041004 )

#define	EVENTS_E_BADDATA	( 0x80041005 )

#define	EVENTS_E_TIMEOUT	( 0x80041006 )

#define	EVENTS_E_DISABLED	( 0x80041007 )


EXTERN_C const IID LIBID_SEOLib;

EXTERN_C const CLSID CLSID_CSEORegDictionary;

#ifdef __cplusplus

class DECLSPEC_UUID("c4df0040-2d33-11d0-a9cf-00aa00685c74")
CSEORegDictionary;
#endif

EXTERN_C const CLSID CLSID_CSEOMimeDictionary;

#ifdef __cplusplus

class DECLSPEC_UUID("c4df0041-2d33-11d0-a9cf-00aa00685c74")
CSEOMimeDictionary;
#endif

EXTERN_C const CLSID CLSID_CSEOMemDictionary;

#ifdef __cplusplus

class DECLSPEC_UUID("c4df0042-2d33-11d0-a9cf-00aa00685c74")
CSEOMemDictionary;
#endif

EXTERN_C const CLSID CLSID_CSEOMetaDictionary;

#ifdef __cplusplus

class DECLSPEC_UUID("c4df0043-2d33-11d0-a9cf-00aa00685c74")
CSEOMetaDictionary;
#endif

EXTERN_C const CLSID CLSID_CSEODictionaryItem;

#ifdef __cplusplus

class DECLSPEC_UUID("2e3a0ec0-89d7-11d0-a9e6-00aa00685c74")
CSEODictionaryItem;
#endif

EXTERN_C const CLSID CLSID_CSEORouter;

#ifdef __cplusplus

class DECLSPEC_UUID("83d63730-94fd-11d0-a9e8-00aa00685c74")
CSEORouter;
#endif

EXTERN_C const CLSID CLSID_CEventLock;

#ifdef __cplusplus

class DECLSPEC_UUID("2e3abb30-af88-11d0-a9eb-00aa00685c74")
CEventLock;
#endif

EXTERN_C const CLSID CLSID_CSEOStream;

#ifdef __cplusplus

class DECLSPEC_UUID("ed1343b0-a8a6-11d0-a9ea-00aa00685c74")
CSEOStream;
#endif

EXTERN_C const CLSID CLSID_CEventManager;

#ifdef __cplusplus

class DECLSPEC_UUID("35172920-a700-11d0-a9ea-00aa00685c74")
CEventManager;
#endif

EXTERN_C const CLSID CLSID_CEventBindingManager;

#ifdef __cplusplus

class DECLSPEC_UUID("53d01080-af98-11d0-a9eb-00aa00685c74")
CEventBindingManager;
#endif

EXTERN_C const CLSID CLSID_CSEOGenericMoniker;

#ifdef __cplusplus

class DECLSPEC_UUID("7e3bf330-b28e-11d0-8bd8-00c04fd42e37")
CSEOGenericMoniker;
#endif

EXTERN_C const CLSID CLSID_CEventMetabaseDatabaseManager;

#ifdef __cplusplus

class DECLSPEC_UUID("8a58cdc0-cbdc-11d0-a9f8-00aa00685c74")
CEventMetabaseDatabaseManager;
#endif

EXTERN_C const CLSID CLSID_CEventUtil;

#ifdef __cplusplus

class DECLSPEC_UUID("a1e041d0-cd73-11d0-a9f8-00aa00685c74")
CEventUtil;
#endif

EXTERN_C const CLSID CLSID_CEventComCat;

#ifdef __cplusplus

class DECLSPEC_UUID("ae1ef300-cd8f-11d0-a9f8-00aa00685c74")
CEventComCat;
#endif

EXTERN_C const CLSID CLSID_CEventRouter;

#ifdef __cplusplus

class DECLSPEC_UUID("9f82f020-f6fd-11d0-aa14-00aa006bc80b")
CEventRouter;
#endif
#endif /* __SEOLib_LIBRARY_DEFINED__ */

/* interface __MIDL_itf_seo_0000_0040 */
/* [local] */ 

SEODLLDEF HRESULT STDAPICALLTYPE MCISInitSEOA(       LPCSTR pszService,
                                                                     DWORD dwVirtualServer,
                                                                     ISEORouter **pprouterResult);
SEODLLDEF HRESULT STDAPICALLTYPE MCISInitSEOW(       LPCWSTR pszService,
                                                                     DWORD dwVirtualServer,
                                                                     ISEORouter **pprouterResult);
SEODLLDEF HRESULT STDAPICALLTYPE SEOCreateDictionaryFromMultiSzA(    DWORD dwCount,
                                                                                                             LPCSTR *ppszNames,
                                                                                                             LPCSTR *ppszValues,
                                                                                                             BOOL bCopy,
                                                                                                             BOOL bReadOnly,
                                                                                                             ISEODictionary **ppdictResult);
SEODLLDEF HRESULT STDAPICALLTYPE SEOCreateDictionaryFromMultiSzW(    DWORD dwCount,
                                                                                                             LPCWSTR *ppszNames,
                                                                                                             LPCWSTR *ppszValues,
                                                                                                             BOOL bCopy,
                                                                                                             BOOL bReadOnly,
                                                                                                             ISEODictionary **ppdictResult);
SEODLLDEF HRESULT STDAPICALLTYPE SEOCreateMultiSzFromDictionaryA(    ISEODictionary *pdictDictionary,
                                                                                                             DWORD *pdwCount,
                                                                                                             LPSTR **pppszNames,
                                                                                                             LPSTR **pppszValues);
SEODLLDEF HRESULT STDAPICALLTYPE SEOCreateMultiSzFromDictionaryW(    ISEODictionary *pdictDictionary,
                                                                                                             DWORD *pdwCount,
                                                                                                             LPWSTR **pppszNames,
                                                                                                             LPWSTR **pppszValues);
SEODLLDEF HRESULT STDAPICALLTYPE MCISGetBindingInMetabaseA(  LPCSTR pszService,
                                                                                             DWORD dwVirtualServer,
                                                                                             REFGUID guidEventSource,
                                                                                             LPCSTR pszBinding,
                                                                                             BOOL bCreate,
                                                                                             BOOL fLock,
                                                                                             ISEODictionary **ppdictResult);
SEODLLDEF HRESULT STDAPICALLTYPE MCISGetBindingInMetabaseW(  LPCWSTR pszService,
                                                                                             DWORD dwVirtualServer,
                                                                                             REFGUID guidEventSource,
                                                                                             LPCWSTR pszBinding,
                                                                                             BOOL bCreate,
                                                                                             BOOL fLock,
                                                                                             ISEODictionary **ppdictResult);
SEODLLDEF HRESULT STDAPICALLTYPE SEOListenForEvent(  ISEORouter *piRouter,
                                                                             HANDLE hEvent,
                                                                             ISEOEventSink *psinkEventSink,
                                                                             BOOL bOnce,
                                                                             DWORD *pdwListenHandle);
SEODLLDEF HRESULT STDAPICALLTYPE SEOCancelListenForEvent(    DWORD dwHandle);
SEODLLDEF HRESULT STDAPICALLTYPE SEOCreateIStreamFromFileA(  HANDLE hFile,
                                                                                             LPCSTR pszFile,
                                                                                             IStream **ppstreamResult);
SEODLLDEF HRESULT STDAPICALLTYPE SEOCreateIStreamFromFileW(  HANDLE hFile,
                                                                                             LPCWSTR pszFile,
                                                                                             IStream **ppstreamResult);
SEODLLDEF HRESULT STDAPICALLTYPE SEOCopyDictionary(  ISEODictionary *pdictIn, ISEODictionary **ppdictResult);
SEODLLDEF HRESULT STDAPICALLTYPE SEOCreateDictionaryFromIStream(     IStream *pstreamIn, ISEODictionary **ppdictResult);
SEODLLDEF HRESULT STDAPICALLTYPE SEOWriteDictionaryToIStream(        ISEODictionary *pdictIn, IStream *pstreamOut);


extern RPC_IF_HANDLE __MIDL_itf_seo_0000_0040_v0_0_c_ifspec;
extern RPC_IF_HANDLE __MIDL_itf_seo_0000_0040_v0_0_s_ifspec;

/* Additional Prototypes for ALL interfaces */

unsigned long             __RPC_USER  BSTR_UserSize(     unsigned long *, unsigned long            , BSTR * ); 
unsigned char * __RPC_USER  BSTR_UserMarshal(  unsigned long *, unsigned char *, BSTR * ); 
unsigned char * __RPC_USER  BSTR_UserUnmarshal(unsigned long *, unsigned char *, BSTR * ); 
void                      __RPC_USER  BSTR_UserFree(     unsigned long *, BSTR * ); 

unsigned long             __RPC_USER  LPSAFEARRAY_UserSize(     unsigned long *, unsigned long            , LPSAFEARRAY * ); 
unsigned char * __RPC_USER  LPSAFEARRAY_UserMarshal(  unsigned long *, unsigned char *, LPSAFEARRAY * ); 
unsigned char * __RPC_USER  LPSAFEARRAY_UserUnmarshal(unsigned long *, unsigned char *, LPSAFEARRAY * ); 
void                      __RPC_USER  LPSAFEARRAY_UserFree(     unsigned long *, LPSAFEARRAY * ); 

unsigned long             __RPC_USER  VARIANT_UserSize(     unsigned long *, unsigned long            , VARIANT * ); 
unsigned char * __RPC_USER  VARIANT_UserMarshal(  unsigned long *, unsigned char *, VARIANT * ); 
unsigned char * __RPC_USER  VARIANT_UserUnmarshal(unsigned long *, unsigned char *, VARIANT * ); 
void                      __RPC_USER  VARIANT_UserFree(     unsigned long *, VARIANT * ); 

/* end of Additional Prototypes */

#ifdef __cplusplus
}
#endif

#endif


