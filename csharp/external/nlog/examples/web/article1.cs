using NLog;

namespace Firma.Komponent
{
    public class Klasa {
        private static Logger logger = LogManager.GetLogger("Firma.Komponent.Klasa");
        // private static Logger logger = LogManager.GetCurrentClassLogger();

        static void Main()
        {
            logger.Info("Informacja");
            logger.Warn("Ostrze�enie {0}", "testowe");
            logger.Error("B��d");
            logger.Fatal("B��d krytyczny");
            // formatowanie w stylu Console.WriteLine()
            logger.Debug("Wynik {0} {1}", 1 + 2, "zzz");
            // formatowanie z u�yciem IFormatProvider
            logger.Debug(CultureInfo.InvariantCulture, "Bie��ca data {0}", DateTime.Now);
        }
    }
}
