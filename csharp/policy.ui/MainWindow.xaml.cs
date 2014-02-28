using System;
using System.IO;
using System.Reactive.Linq;
using System.Runtime.CompilerServices;
using System.Security.Cryptography.X509Certificates;
using System.Text;
using System.Threading.Tasks;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Documents;
using System.Windows.Media;
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
        public MainWindow()
        {
            InitializeComponent();
            ClearDesignerText();
            LoadSettings();


            var textChangedObservable = Observable
                .FromEventPattern<TextChangedEventArgs>(PolicyRichTextBox, "TextChanged")
                .Throttle(TimeSpan.FromMilliseconds(500))
                .Select(evt => ((RichTextBox) evt.Sender));

            textChangedObservable.ObserveOnDispatcher().Subscribe(rtxBox => ParseLexicon(rtxBox));
            }


        private void ParseLexicon(RichTextBox rtxBox)
        {
            var textRange = new TextRange(rtxBox.Document.ContentStart, rtxBox.Document.ContentEnd);
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
                    var fs = new FileStream(PolicyFileLocation.Text.ToString(), FileMode.Open, FileAccess.Read);
                    using (fs)
                    {
                        var textRange = new TextRange(PolicyRichTextBox.Document.ContentStart,
                            PolicyRichTextBox.Document.ContentEnd);
                        textRange.Load(fs, DataFormats.Text);
                    }
                }
            }
            else
            {
                PolicyFileLocation.Text = string.Empty;
            }
        }

        
        private void SaveSettings()
        {
            Properties.Settings.Default.PolicyFile = PolicyFileLocation.Text;
            Properties.Settings.Default.CertFile = CertificateLocation.Text;
            Properties.Settings.Default.Save();
        }

        private void FileOpen_Click(object sender, RoutedEventArgs e)
        {
            var openFileDialog = new OpenFileDialog();
            var fileInfo = new FileInfo(PolicyFileLocation.Text);
            if (fileInfo.DirectoryName != null && Directory.Exists(fileInfo.DirectoryName))
            {
                openFileDialog.InitialDirectory = fileInfo.DirectoryName;
            }
            openFileDialog.Filter = "Direct Policy files (*.dpol, *.txt)|*.dpol;*.txt";
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

        private void CertBrowseButton_Click(object sender, RoutedEventArgs e)
        {
            var openFileDialog = new OpenFileDialog();
            var fileInfo = new FileInfo(CertificateLocation.Text);
            if (fileInfo.DirectoryName != null && Directory.Exists(fileInfo.DirectoryName))
            {
                openFileDialog.InitialDirectory = fileInfo.DirectoryName;
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
            var ms = new MemoryStream();
            
            var textRange = new TextRange(PolicyRichTextBox.Document.ContentStart,
                PolicyRichTextBox.Document.ContentEnd);
            textRange.Save(ms, DataFormats.Text);
            ms.Position = 0;
            var cert = new X509Certificate2(CertificateLocation.Text);
            ICompiler compiler = new StackMachineCompiler();
            IPolicyFilter filter = new DefaultPolicyFilter(compiler, new StackMachine(),
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

    }
}
