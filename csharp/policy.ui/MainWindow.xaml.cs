using System;
using System.IO;
using System.Reactive.Linq;
using System.Security.Cryptography.X509Certificates;
using System.Text;
using System.Threading.Tasks;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Documents;
using System.Windows.Media;
using Health.Direct.Common.Policies;
using Health.Direct.Policy.Extensions;
using Health.Direct.Policy.Impl;
using Health.Direct.Policy.Machine;
using Microsoft.Win32;

namespace Health.Direct.Policy.UI
{
    /// <summary>
    /// Interaction logic for MainWindow.xaml
    /// </summary>
    public partial class MainWindow : Window
    {
        private const string DirectPolicyDataFolder = @"DirectProject\Policy\Policies";
        private const string DirectPolicyCertFolder = @"DirectProject\Policy\Certs";
        private const string DirectPolicyFileExtFilter = "Direct Policy files (*.dpol, *.txt)|*.dpol;*.txt";

        public MainWindow()
        {
            InitializeComponent();
            ClearDesignerText();

            var textChangedObservable = Observable
                .FromEventPattern<TextChangedEventArgs>(PolicyRichTextBox, "TextChanged")
                .Throttle(TimeSpan.FromMilliseconds(500))
                .Select(evt => ((RichTextBox)evt.Sender));

            textChangedObservable.ObserveOnDispatcher().Subscribe(rtxBox => ParseLexicon(rtxBox));

            LoadSettings();
        }


        private void ParseLexicon(RichTextBox rtxBox)
        {
            var textRange = new TextRange(rtxBox.Document.ContentStart, rtxBox.Document.ContentEnd);
            if (string.IsNullOrEmpty(textRange.Text))
            {
                return;
            }
            CallLexiconParser(textRange.Text, rtxBox);
        }

        private async void CallLexiconParser(string lexicon, RichTextBox rtxBox)
        {
            try
            {
                IPolicyExpression expression = await ParseLexicon(lexicon);
                var textRange = new TextRange(rtxBox.Document.ContentStart, rtxBox.Document.ContentEnd);
                textRange.ApplyPropertyValue(TextElement.ForegroundProperty, Brushes.Black);
            }
            catch (PolicyGrammarException)
            {
                var textRange = new TextRange(rtxBox.Document.ContentStart, rtxBox.Document.ContentEnd);
                textRange.ApplyPropertyValue(TextElement.ForegroundProperty, Brushes.Red);
            }
        }

        private Task<IPolicyExpression> ParseLexicon(string lexicon)
        {
            return Task.Run(() =>
            {
                var parser = new SimpleTextV1LexiconPolicyParser();
                return parser.Parse(lexicon.ToStream());
            });
        }

        private void Window_Closing(object sender, System.ComponentModel.CancelEventArgs e)
        {
            SaveSettings();
        }


        private void ClearDesignerText()
        {
            PolicyRichTextBox.Document.Blocks.Clear();
            CertificateLocation.Text = string.Empty;
            ValidationResults.Text = string.Empty;
        }

        private void LoadSettings()
        {
            if (!string.IsNullOrEmpty(Properties.Settings.Default.CertFile))
            {
                CertificateLocation.Text = Properties.Settings.Default.CertFile;
            }

            if (!string.IsNullOrEmpty(Properties.Settings.Default.PolicyFile))
            {
                PolicyFileLocation.Text = Properties.Settings.Default.PolicyFile;
                if (File.Exists(Properties.Settings.Default.PolicyFile))
                {
                    var fs = new FileStream(PolicyFileLocation.Text, FileMode.Open, FileAccess.Read);
                    using (fs)
                    {
                        var textRange = new TextRange(PolicyRichTextBox.Document.ContentStart,
                            PolicyRichTextBox.Document.ContentEnd);
                        textRange.Load(fs, DataFormats.Text);
                        return;
                    }
                }
            }
            PolicyFileLocation.Text = string.Empty;
        }


        private void SaveSettings()
        {
            Properties.Settings.Default.PolicyFile = PolicyFileLocation.Text;
            Properties.Settings.Default.CertFile = CertificateLocation.Text;
            Properties.Settings.Default.Save();
        }



        private FileInfo GetFileInfo(string path)
        {
            if (String.IsNullOrEmpty(path))
            {
                return null;
            }
            return new FileInfo(path);
        }

        private void CertBrowseButton_Click(object sender, RoutedEventArgs e)
        {
            var openFileDialog = new OpenFileDialog();
            var fileInfo = GetFileInfo(CertificateLocation.Text);
            if (fileInfo != null
                && fileInfo.DirectoryName != null
                && Directory.Exists(fileInfo.DirectoryName))
            {
                openFileDialog.InitialDirectory = fileInfo.DirectoryName;
            }
            else
            {
                openFileDialog.InitialDirectory = ProgramDataCertsFolder;
            }
            openFileDialog.ReadOnlyChecked = true;
            openFileDialog.Filter = "Certificate files (*.der, *.cer)|*.der;*.cer";
            if (openFileDialog.ShowDialog() == true)
            {
                CertificateLocation.Text = openFileDialog.FileName;
            }
        }

        private void ValidateCertButton_Click(object sender, RoutedEventArgs e)
        {
            if (string.IsNullOrEmpty(CertificateLocation.Text))
            {
                return;
            }
            var ms = new MemoryStream();

            var textRange = new TextRange(PolicyRichTextBox.Document.ContentStart,
                PolicyRichTextBox.Document.ContentEnd);
            textRange.Save(ms, DataFormats.Text);
            ms.Position = 0;
            var cert = new X509Certificate2(CertificateLocation.Text);
            ICompiler compiler = new StackMachineCompiler();
            compiler.ReportModeEnabled = true;
            IPolicyFilter filter = new PolicyFilter(compiler, new StackMachine(),
                new SimpleTextV1LexiconPolicyParser());
            var sb = new StringBuilder();
            sb.Append("Validation run at ")
                .AppendLine(DateTime.Now.ToString("ddd, MMM d yyyy HH:mm:ss"))
                .AppendLine();
            try
            {
                if (filter.IsCompliant(cert, ms))
                {
                    sb.Append("Certificate is compliant with the provided policy.");
                }
                else
                {
                    sb.AppendLine("Certificate is NOT compliant with the provided policy.").AppendLine();
                    foreach (string item in compiler.CompiliationReport)
                    {
                        sb.AppendLine(item);
                    }
                }
            }
            catch (PolicyRequiredException ex)
            {
                sb.AppendLine("Validation Successful")
                    .AppendLine("Certificate is missing a required field \t")
                    .AppendLine(ex.Message);
            }
            catch (PolicyGrammarException ex)
            {
                sb.AppendLine("Validation Failed:")
                    .AppendLine("Error compiling policy\t")
                    .AppendLine(ex.Message);
            }
            catch (Exception ex)
            {
                sb.AppendLine("Validation Failed:")
                .AppendLine("Error compiling or proccessing policy\t" + ex.Message)
                .AppendLine(ex.StackTrace);
            }

            ValidationResults.Text = sb.ToString();
        }

        private void OpenCommandBinding_Executed(object sender, RoutedEventArgs e)
        {
            var openFileDialog = new OpenFileDialog();
            var fileInfo = GetFileInfo(PolicyFileLocation.Text);
            if (fileInfo != null
                && fileInfo.DirectoryName != null
                && Directory.Exists(fileInfo.DirectoryName))
            {
                openFileDialog.InitialDirectory = fileInfo.DirectoryName;
            }
            else
            {
                openFileDialog.InitialDirectory = ProgramDataPoliciesFolder;
            }
            openFileDialog.Filter = DirectPolicyFileExtFilter;
            if (openFileDialog.ShowDialog() == true)
            {
                PolicyFileLocation.Text = openFileDialog.FileName;
                var fs = new FileStream(PolicyFileLocation.Text, FileMode.Open, FileAccess.Read);
                using (fs)
                {
                    var textRange = new TextRange(PolicyRichTextBox.Document.ContentStart,
                        PolicyRichTextBox.Document.ContentEnd);
                    textRange.Load(fs, DataFormats.Text);
                }
            }
        }

        private void SaveCommandBinding_Executed(object sender, RoutedEventArgs e)
        {
            if (!GetSaveFileLocation(PolicyFileLocation.Text))
            {
                return;
            }

            var fs = new FileStream(PolicyFileLocation.Text, FileMode.Create, FileAccess.Write);
            using (fs)
            {
                var textRange = new TextRange(PolicyRichTextBox.Document.ContentStart,
                    PolicyRichTextBox.Document.ContentEnd);
                textRange.Save(fs, DataFormats.Text);
            }
        }

        bool GetSaveFileLocation(string fileName)
        {
            if (string.IsNullOrEmpty(fileName) || !File.Exists(fileName))
            {
                var saveFileDialog = new SaveFileDialog();

                saveFileDialog.InitialDirectory = ProgramDataPoliciesFolder;
                saveFileDialog.Filter = DirectPolicyFileExtFilter;

                if (saveFileDialog.ShowDialog() == true)
                {
                    PolicyFileLocation.Text = saveFileDialog.FileName;
                    return true;
                }
                return false;
            }
            return true;
        }

        private static string ProgramDataPoliciesFolder
        {
            get
            {
                string folderPath = Path.Combine(Environment.GetFolderPath(Environment.SpecialFolder.CommonApplicationData),
                    DirectPolicyDataFolder);
                if (!Directory.Exists(folderPath))
                {
                    Directory.CreateDirectory(folderPath);
                }
                return folderPath;
            }
        }

        private static string ProgramDataCertsFolder
        {
            get
            {
                string folderPath = Path.Combine(Environment.GetFolderPath(Environment.SpecialFolder.CommonApplicationData),
                    DirectPolicyCertFolder);
                if (!Directory.Exists(folderPath))
                {
                    Directory.CreateDirectory(folderPath);
                }
                return folderPath;
            }
        }

        private void CloseCommandBinding_Executed(object sender, RoutedEventArgs e)
        {
            Application.Current.Shutdown();
        }

        private void NewCommandBinding_Executed(object sender, RoutedEventArgs e)
        {
            var fileInfo = GetFileInfo(PolicyFileLocation.Text);
            if (!GetSaveFileLocation(fileInfo.DirectoryName))
            {
                return;
            }

            var fs = new FileStream(PolicyFileLocation.Text, FileMode.Create, FileAccess.Write);
            using (fs)
            {
                PolicyRichTextBox.Document.Blocks.Clear();
                var textRange = new TextRange(PolicyRichTextBox.Document.ContentStart,
                    PolicyRichTextBox.Document.ContentEnd);
                textRange.Save(fs, DataFormats.Text);
            }

        }

        private void SaveAsCommandBinding_Executed(object sender, RoutedEventArgs e)
        {
            var fileInfo = GetFileInfo(PolicyFileLocation.Text);
            if (!GetSaveFileLocation(fileInfo.DirectoryName))
            {
                return;
            }

            var fs = new FileStream(PolicyFileLocation.Text, FileMode.Create, FileAccess.Write);
            using (fs)
            {
                var textRange = new TextRange(PolicyRichTextBox.Document.ContentStart,
                    PolicyRichTextBox.Document.ContentEnd);
                textRange.Save(fs, DataFormats.Text);
            }
        }
    }
}
