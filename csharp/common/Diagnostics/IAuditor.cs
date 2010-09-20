namespace NHINDirect.Diagnostics
{
    public interface IAuditor
    {
        void Log(string category, string message);
    }
}