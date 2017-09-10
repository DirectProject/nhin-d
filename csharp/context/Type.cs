namespace Health.Direct.Context
{
    /// <summary>
    /// Reprsents a <c>type</c>.
    /// </summary>
    public class Type
    {
        /// <summary>
        /// 
        /// </summary>
        public string Category { get; set; }
        /// <summary>
        /// 
        /// </summary>
        public string Action { get; set; }

        /// <summary>
        /// Format <c>type-elment value as category/action</c>.
        /// </summary>
        /// <returns></returns>
        public override string ToString()
        {
            return $"{Category}/{Action}";
        }
    }
}