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

            // w przypadku du�ej liczby parametr�w warto sprawdzi�
            // czy komunikat ma szans� by� gdziekolwiek zapisany
            if (logger.IsDebugEnabled) {
                int x = 3;
                int y = 5;
                
                logger.Debug("x={0}, y={1}", x, y);
            }

            // formatowanie w stylu Console.WriteLine()
            logger.Debug("Wynik {0} {1}", 1 + 2, "zzz");

            // formatowanie z u�yciem IFormatProvider
            logger.Debug(CultureInfo.InvariantCulture, 
                    "Bie��ca data {0}", DateTime.Now);
        }
    }
}
