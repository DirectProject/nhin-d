using System;
using System.Text.Json;
using System.Text.Json.Serialization;

namespace Health.Direct.MdnMonitor
{
    /// <summary>
    /// Helper for AppSettings.json deserialization of a <see cref="TimeSpan"/> string
    /// </summary>
    public class TimeSpanConverter : JsonConverter<TimeSpan>
    {
        /// <summary>
        /// Simple Timespan.Parse of a string. 
        /// </summary>
        /// <param name="reader"></param>
        /// <param name="typeToConvert"></param>
        /// <param name="options"></param>
        /// <returns></returns>
        public override TimeSpan Read(ref Utf8JsonReader reader, Type typeToConvert, JsonSerializerOptions options)
        {
            return TimeSpan.Parse(reader.GetString() ?? string.Empty);
        }

        /// <summary>
        /// Write TimeSpan as string.
        /// </summary>
        /// <param name="writer"></param>
        /// <param name="value"></param>
        /// <param name="options"></param>
        public override void Write(Utf8JsonWriter writer, TimeSpan value, JsonSerializerOptions options)
        {
            writer.WriteStringValue(value.ToString());
        }
    }
}
