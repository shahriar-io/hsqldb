#region licence

/* Copyright (c) 2001-2009, The HSQL Development Group
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * Neither the name of the HSQL Development Group nor the names of its
 * contributors may be used to endorse or promote products derived from this
 * software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL HSQL DEVELOPMENT GROUP, HSQLDB.ORG,
 * OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

#endregion

#region Using
using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Resources; 
using System.Text;
#endregion

namespace System.Data.Hsqldb.Common.Attribute
{
    #region ResCategoryAttribute
    /// <summary>
    /// Specifies the name of the category in which to group a property or
    /// event when displayed in a <see cref="T:System.Windows.Forms.PropertyGrid"/>
    /// control set to Categorized mode.
    /// </summary> 
    [AttributeUsage(AttributeTargets.All)]
    public class ResCategoryAttribute : CategoryAttribute
    {
        /// <summary>
        /// 
        /// </summary>
        public static class ResKey
        {
            /// <summary>
            /// 
            /// </summary>
            public const string ForData = "ResCategory_Data";
        }

        #region Fields
        private static readonly ResourceManager rm = Properties.Resources.ResourceManager; 
        #endregion

        #region Constructors

        #region ResCategoryAttribute(string)
        /// <summary>
        /// Constructs a new <c>ResCategoryAttribute</c> instance.
        /// </summary>
        /// <param name="resourceKey">The resource key.</param>
        public ResCategoryAttribute(string resourceKey)
            : base(resourceKey)
        {
        } 
        #endregion
        
        #endregion

        #region Method Overrides

        #region GetLocalizedString(string)
        /// <summary>
        /// Looks up the localized name of the specified category.
        /// </summary>
        /// <param name="value">The identifer for the category to look up.</param>
        /// <returns>
        /// The localized name of the category, or null if a localized name does not exist.
        /// </returns>
        protected override string GetLocalizedString(string value)
        {
            return rm.GetString(value);
        } 
        #endregion

        #endregion
    } 
    #endregion

}
