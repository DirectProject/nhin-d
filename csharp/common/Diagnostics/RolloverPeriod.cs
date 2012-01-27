namespace Health.Direct.Common.Diagnostics
{
    ///<summary>
    /// Files are rolled based on this period. 
    ///</summary>
    public enum RolloverPeriod
    {
        ///<summary>
        /// Don't rollover based on time.
        ///</summary>
        None = 0,
        ///<summary>
        /// Rollover daily.
        ///</summary>
        Day,
        ///<summary>
        /// Rollover every hour.
        ///</summary>
        Hour,
        ///<summary>
        /// Rollover every minute.
        ///</summary>
        Minute,
        ///<summary>
        /// Rollover every month.
        ///</summary>
        Month,
        ///<summary>
        /// Rollover every year.
        ///</summary>
        Year
    }
}