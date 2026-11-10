package org.jyutping.jyutping

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.jyutping.jyutping.models.KeypadCombo
import org.jyutping.jyutping.models.NineKeySegmenter
import org.jyutping.jyutping.models.NineKeySyllable
import org.jyutping.jyutping.models.Segmenter
import org.jyutping.jyutping.models.Structure
import org.jyutping.jyutping.models.VirtualInputKey
import org.jyutping.jyutping.models.conjoinedCode
import org.jyutping.jyutping.utilities.DatabasePreparer

@RunWith(AndroidJUnit4::class)
class StructureTest {

        @Before
        fun prepareDatabase() {
                DatabasePreparer.prepare(InstrumentationRegistry.getInstrumentation().targetContext)
        }

        @Test
        fun nineKeyComponentsReturnCharacterReadingAndComponentMark() = runBlocking {
                val combos = combos(685685)
                val segmentation = NineKeySegmenter.segment(combos)
                val items = Structure.nineKeyReverseLookup(combos, segmentation + segmentation)
                val item = items.first { it.text == "林" && it.romanization == "lam4" }
                assertEquals(combos.size, item.inputCount)
                assertEquals("muk muk", item.mark)
                assertEquals(items.size, items.distinct().size)
        }

        @Test
        fun nineKeyLookupRequiresCompleteInput() = runBlocking {
                val combos = combos(685685685)
                val items = Structure.nineKeyReverseLookup(combos, NineKeySegmenter.segment(combos))
                assertTrue(items.any { it.text == "森" && it.mark == "muk muk muk" && it.inputCount == 9 })
                assertFalse(items.any { it.text == "林" })
                val extra = combos(6856859)
                assertFalse(Structure.nineKeyReverseLookup(extra, NineKeySegmenter.segment(extra)).any { it.text == "林" })
        }

        @Test
        fun nineKeyAlternateSpellingsResolveCanonicalComponents() = runBlocking {
                val combos = combos(663494) // noei + zi -> neoi + zi
                val items = Structure.nineKeyReverseLookup(combos, NineKeySegmenter.segment(combos))
                assertTrue(items.any { it.text == "好" && it.romanization == "hou2" && it.mark == "neoi zi" && it.inputCount == combos.size })
        }

        @Test
        fun nineKeyAliasesRetainEnteredKeyCount() = runBlocking {
                val syllable = NineKeySyllable(
                        aliasCode = 22,
                        originCode = 685,
                        serialAliasCode = keys("aa").conjoinedCode,
                        serialOriginCode = keys("muk").conjoinedCode
                )
                val items = Structure.nineKeyReverseLookup(combos(2222), listOf(listOf(syllable, syllable)))
                assertTrue(items.any { it.text == "林" && it.mark == "muk muk" && it.inputCount == 4 })
        }

        @Test
        fun nineKeyLookupHandlesEmptyAndUnmatchedInput() = runBlocking {
                assertTrue(Structure.nineKeyReverseLookup(emptyList(), emptyList()).isEmpty())
                val combos = combos(999)
                assertTrue(Structure.nineKeyReverseLookup(combos, NineKeySegmenter.segment(combos)).isEmpty())
                assertTrue(Structure.nineKeyReverseLookup(combos(685685), emptyList()).isEmpty())
        }

        @Test
        fun regularComponentLookupStillWorks() {
                val keys = keys("mukmuk")
                val items = Structure.reverseLookup(keys, Segmenter.segment(keys))
                assertTrue(items.any { it.text == "林" && it.romanization == "lam4" && it.inputCount == 6 })
        }

        private fun combos(digits: Int): List<KeypadCombo> = digits.toString().map { requireNotNull(KeypadCombo.elementOf(it.digitToInt())) }

        private fun keys(text: String): List<VirtualInputKey> = text.map { requireNotNull(VirtualInputKey.matchVirtualInputKey(it)) }
}
