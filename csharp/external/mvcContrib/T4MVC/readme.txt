T4MVC readme file
Find latest version and documentation at http://mvccontrib.codeplex.com/wikipage?title=T4MVC
Discuss on the T4MVC forum: http://forums.asp.net/1215.aspx

Maintained by David Ebbo, with much feedback from the MVC community (thanks all!)
david.ebbo@microsoft.com
http://twitter.com/davidebbo
http://blogs.msdn.com/davidebb

Related blog posts:
http://blogs.msdn.com/davidebb/archive/tags/T4MVC/default.aspx
http://www.hanselman.com/blog/TheWeeklySourceCode43ASPNETMVCAndT4AndNerdDinner.aspx

Feel free to use and modify to fit your needs.

This T4 template for ASP.NET MVC apps creates strongly typed helpers that eliminate the use
of literal strings when referring the controllers, actions and views.

To use it, simply copy it and T4MVC.settings.t4 to the root of your MVC application.

This will enable the following scenarios:

Refer to controller, action and view names as shown in these examples:
    - MVC.Dinners.Name: "Dinners" (controller name). 
    - MVC.Dinners.Views.DinnerForm: "DinnerForm" (view name)
    - MVC.Dinners.Actions.Delete: "Delete" (action name)

Strong type certain scenarios that refer to controller actions.  e.g.
    - Html.ActionLink("Delete Dinner", MVC.Dinners.Delete(Model.DinnerID))
    - Url.Action(MVC.Dinners.Delete(Model.DinnerID))
    - RedirectToAction(MVC.Dinners.Delete(dinner.DinnerID))
    - Route defaults e.g.
            routes.MapRoute(
                "UpcomingDinners", 
                "Dinners/Page/{page}", 
                MVC.Dinners.Index(null)
            );

Refer to your static images and script files with strong typing, e.g.
    Instead of <img src="/Content/nerd.jpg" ...>, you can write <img src="<%= Links.Content.nerd_jpg %>" ...>
    Instead of <script src="/Scripts/Map.js" ...>, you can write <script src="<%= Links.Scripts.Map_js %>" ...>
    Or if the file name is dynamic, you can write: Links.Content.Url("foo.jpg")

KNOWN ISSUES:
- Users running VisualSVN have reported some errors when T4MVC tries to change actions to virtual and controllers to partial.
  The suggestion when that happens is to manually make those changes.  This is just a one time thing you need to do.
- It will not locate controllers that live in a different project or assembly
- Compile error when folder under Content contains subfolder with same name as itself (http://blogs.msdn.com/davidebb/archive/2010/01/04/t4mvc-2-6-10-fluent-route-value-api-shorter-way-to-refer-to-action-and-more.aspx#9952727)

TODO:
- Support object parameters http://stackoverflow.com/questions/2381455/strongly-typed-t4mvc-action-actionlink
- Add static file support for each area (from Jeremy Brayton)
- Support favicon.ico in the Links
- Support controllers in different project (http://forums.asp.net/t/1500812.aspx)

HISTORY:

2.6.22 (07-31-2010):
- Handled System.NotImplementedException if project type (e.g. Installer) does not implement CodeModel property

2.6.21 (07-20-2010):
- Fix to the partial extension feature added in 2.6.20 to deal with conflicts

2.6.20 (07-19-2010):
- Added extension methods to render partial views (see http://www.weirdlover.com/2010/05/12/t4mvc-extension-for-mvc-partials/)
    e.g. Html.RenderPartial("Map", Model.Dinner); --> Html.RenderMap(Model.Dinner);
    Can be turned off in settings file via ExplicitHtmlHelpersForPartials flag.

2.6.15 (05-16-2010):
- Added missing MapRoute overloads to support namespaces
- Added MapRoute extensions on AreaRegistrationContext to support areas. Had to name them MapRouteArea to avoid clash with existing MapRoute methods :(

2.6.14 (05-06-2010):
- Added UseLowercaseRoutes flag to lower case the area, controller and action names in routes
- Added support for Views folders that don't match a controller name.
- Added optimization to not regenerate files when the controller has not changed since the last generation
- Turn AlwaysKeepTemplateDirty to false by default. We now have an AddIn which provides a better solution to this

2.6.13 (03-08-2010):
- Added AddTimestampToStaticLinks flag to T4MVC.settings.t4 to generate static links that change when the file changes.
- Added support for [Bind(Prefix = "newParamName")] attribute so it generates the correct route value
- Added an AddRouteValues overload that takes NameValueCollection, e.g.
	- MVC.Home.About().AddRouteValues(Request.QueryString)
- Added MapRoute overload that supports contraints
- Removed some logic to support VS2010 Beta 2.  VS2010 RC or later should now be used.

2.6.12 (01-15-2010):
- Fixed issue where some special project types (e.g. DB projects) were throwing while enumerating over the list
- Changed to use GeneratedCode attribute instead of the less correct CompilerGenerated
- Fixed CompilerGenerated/DebuggerNonUserCode attributes so they don't incorrectly affect the control classes
- Change to avoid adding area to route if the app is not using areas

2.6.11 (01-10-2010):
- Added ExcludedStaticFileExtensions setting to T4MVC.settings.t4 to list extensions for which static links should not be generated.
- Support configurable name for IT4MVCActionResult, and it being defined externally (for sharing pourpose)
- Added DebuggerNonUserCode attribute to generated classes
- Improved error handling when trying to call T4MVC method with real ActionResult (previous threw InvalidCast)
- Fix issue when overridden action methods were incorrectly marked as virtual

2.6.10 (01-04-2010):
- Added support for adding arbitrary route parameters to T4MVC actions. e.g.
	- MVC.Home.About().AddRouteValue("fooKey", bar.Foo.Key)
	- MVC.Home.About().AddRouteValues(new {fooKey = bar.Foo.Key, barKey = bar.Key})
- Added shorter to refer to controller actions from with the controller itself. e.g.
	- return RedirectToAction(MVC.MyController.Actions.About());		BECOMES:
	- return RedirectToAction(Actions.About());
- What was previously called Actions was renamed to ActionNames (note, that's a BREAKING CHANGE if you used Actions before!)
- Ignore controller methods that are marked as [NonAction]
- Renamed IT4MVCActionResult.RouteValues to avoid conflict (this should not break anything, as it's only used internally)

2.6.03 (12-10-2009):
- Generate full view paths to allow cross controller references
  e.g. MVC.Dinners.Views.DinnerForm is now "~/Views/Dinners/DinnerForm.ascx" instead of just "DinnerForm"
- Fix compile error when a view name is a language keyword (e.g. string.ascx)

2.6.02 (12-04-2009):
- Added way to get area name from both Area and Controller objects
	e.g. MVC.MyArea.Name and MVC.MyArea.MyController.Area
- Added support for controllers in the default namespace (i.e. no namespace)
- Always include the area in the route data, even when it's null/empty

2.6.01 (12-02-2009)
- Fix compile error when a custom ActionResult type has a ctor that takes a value type

2.6.00 (11-28-2009)
- Added support for MVC 2 Areas
- Fixed issue where non-existing RenderAction method gets generated on VS2010 Beta 2
- Added check to give proper error when attempting to run T4MVC outside VS (e.g. from TextTransform.exe)

2.5.02 (11-24-2009)
- Change links in comment to point to new T4MVC home and forum
- Fix scenario where a View folder as a name that's a C# keyword

2.5.01 (11-20-2009)
- Added support for Html.RenderAction and Html.Action (see http://haacked.com/archive/2009/11/18/aspnetmvc2-render-action.aspx)
- Fix null ref exception issue when custom ActionResult type doesn't have any explicit ctors

2.5.00 (11-16-2009)
- Incorporated Damien Guard's multiple output manager to (optionally) split output from T4MVC into separate files to improve source control
- Added support for minified javascript files in production
- Fixed bug occurring when no action is of type ActionResult

2.4.04 (10-15-2009)
- Added support for MVC 2 by detecting the version and generating slightly different code

2.4.03 (10-02-2009)
- Added ProcessVirtualPath method to T4MVC.settings.t4 so user can write custom logic to modify client URL's
- Greatly simplified GetProjectContainingT4File logic by using FindProjectItem().
- Renamed generated classes to be CLS compliant
- Moved most of the doc and versioning comments from T4MVC.tt into the readme.txt file, as it was getting a bit long.

2.4.02 (09-02-2009)
- Added a setting in T4MVC.settings.t4 to set the namespace that Links get generated in
- Added pragma to prevent compiler from complaining about missing Xml comments
- Added <auto-generated /> comment to disable StyleCop in generated file
- Fixed issue when using a custom ResultType in a custom namespace. Now fully qualifying result types.

2.4.01 (07-29-2009):
- Put all the generated code in a T4MVC #region. This is useful to tell tools like ReSharper to ignore it.
- Fixed issue where controller methods returning generic types cause template to blow up
- Added a setting in T4MVC.settings.t4 to turn off the behavior that always keeps the template dirty

2.4.00 (07-28-2009):
- Added support for configurable settings in a separate T4MVC.settings.t4 file
- Added a parameter-less pseudo-action for every action that doesn't already have a parameter-less overload
- Added support for having T4MVC.tt in a sub folder instead of always at the root of the project
- Fixed issue when a base controller doesn't have a default ctor
- Added T4Extensions into System.Web.Mvc namespace to fix ambiguous resolution issue 
- Misc cleanup

2.3.01 (07-10-2009):
- Fixed issue with [ActionName] attribute set to non literal string values (e.g. [ActionName(SomeConst + "Abc")])
- Fixed duplication issue when partial controller classes have a base type which contains action methods
- Skip App_LocalResources when processing views
- Cleaned up rendering logic

2.3.00 (07-07-2009):
- Added support for sub view folders
- Added support for [ActionName] attribute
- Improved handling when the controller comes from a different project
- Don't try to process generic controller classes

2.2.03 (07-06-2009):
- Added support for action methods defined on controller base classes
- Improved error handling when not able to change actions to virtual and controllers to partial

2.2.02 (07-01-2009):
- Fixed break caused by incorrect support for derived ActionResult types in 2.2.01
- Fixed issue with duplicate view tokens getting generated when you have both foo.aspx and foo.ascx

2.2.01 (07-01-2009):
- Added support for action methods that return a type derived from ActionResult (as opposed to exactly an ActionResult)
- Fixed issue when controller is using partial classes
- Fixed folder handling logic to deal with generated files
- Fixed issue with folder names that are C# keyword
- Throw NotSupportedException instead of NotImplementedException to avoid being viewed as a TODO

2.2.00 (06-30-2009):
- Added strongly typed support to MapRoute
- Changed constructor generation to avoid confusing IoC containers
- Fixed issue with empty Content folder
- Fixed issue with abstract controller base classes

2.1.00 (06-29-2009):
- Added Html.BeginForm overloads that use the strongly typed pattern
- Added Url() helpers on static resources to increase flexibility
- Changed generated constants (view and action names, static files) to be readonly strings
- Fixed null ref exception in Solution Folder logic

2.0.04 (06-28-2009):
- Fixed issue with files and folders with names starting with a digit

2.0.03 (06-27-2009):
- Rework code element enumeration logic to work around a VS2010 issue. The template should now work with VS2010 beta 1!
- Reduced some redundancy in the generated code

2.0.02 (06-27-2009):
- Added ActionLink overloads that take object instead of dictionary (from both Html and Ajax)

2.0.01 (06-26-2009):
- Fixed issue with files and folders with invalid identifier characters (e.g. spaces, '-', '.')

2.0.00 (06-26-2009): as described in http://blogs.msdn.com/davidebb/archive/2009/06/26/the-mvc-t4-template-is-now-up-on-codeplex-and-it-does-change-your-code-a-bit.aspx
- Added support for refactoring in Action methods
- The T4 file automatically runs whenever you build, instead of being done manually
- Support for strongly typed links to static resources
- Fix: supports controllers that are in sub-folders of the Controllers folder and not directly in there
- Fix: works better with nested solution folder 
- Random other small fixes

1.0.xx (06-17-2009): the original based on this post
    http://blogs.msdn.com/davidebb/archive/2009/06/17/a-new-and-improved-asp-net-mvc-t4-template.aspx




To use T4MVC, simply drop T4MVC.tt and T4MVC.settings.t4 at the root of your MVC project.

For more information, please see comment at the top of T4MVC.tt
