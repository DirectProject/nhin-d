using System;
using System.IO;
using System.Security.Cryptography.X509Certificates;
using System.Windows;
using System.Windows.Documents;
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
            bool succeed = false;
            string resultMessage = string.Empty;
            using (ms)
            {
                var textRange = new TextRange(PolicyRichTextBox.Document.ContentStart,
                    PolicyRichTextBox.Document.ContentEnd);
                textRange.Save(ms, DataFormats.Text);
                ms.Position = 0;
                var cert = new X509Certificate2(CertificateLocation.Text);
                IPolicyFilter filter = new DefaultPolicyFilter(new StackMachineCompiler(), new StackMachine(), new SimpleTextV1LexiconPolicyParser());
                try
                {
                    succeed = filter.IsCompliant(cert, ms);
                }
                catch (Exception ex)
                {
                    resultMessage = ex.ToString();
                }
            }

            if (succeed)
            {
                ValidationResults.Text = "Succeeded.";
            }
            else
            {
                ValidationResults.Text = string.Format("Falied. \r\n {0}", resultMessage);
            }
            
        }
    }
}
