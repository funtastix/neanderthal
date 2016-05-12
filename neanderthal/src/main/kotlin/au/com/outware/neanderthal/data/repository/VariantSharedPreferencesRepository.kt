package au.com.outware.neanderthal.data.repository

import android.content.Context
import android.content.SharedPreferences
import au.com.outware.neanderthal.data.model.Variant
import au.com.outware.neanderthal.data.repository.VariantRepository
import au.com.outware.neanderthal.util.CharSequenceDeserializer
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import org.jetbrains.anko.defaultSharedPreferences
import java.util.*

/**
 * @author timmutton
 */
class VariantSharedPreferencesRepository(val klass: Class<out Any>,
                                         val context: Context,
                                         baseVariants: Map<String, Any>,
                                         defaultVariant: String): VariantRepository {
    companion object {
        const val KEY_PREFIX = "neanderthal_"
        const val VARIANT_LIST = KEY_PREFIX + "variant_list"
        const val CURRENT_VARIANT = KEY_PREFIX + "current_variant"
    }

    private val sharedPreferences: SharedPreferences
    private val editor: SharedPreferences.Editor
    private val gson: Gson = GsonBuilder().registerTypeAdapter(CharSequence::class.java, CharSequenceDeserializer()).create()

    init {
        sharedPreferences = context.defaultSharedPreferences
        editor = sharedPreferences.edit()

        if (!sharedPreferences.contains(VARIANT_LIST)) {
            editor.putStringSet(VARIANT_LIST, baseVariants.keys)
            for(variant in baseVariants) {
                editor.putString(KEY_PREFIX + variant.key, gson.toJson(variant.value))
            }
            editor.putString(CURRENT_VARIANT, defaultVariant)
            editor.apply()
        }
    }

    override fun addVariant(variant: Variant) {
        val variantList = sharedPreferences.getStringSet(VARIANT_LIST, null)
        // TODO: confirm this returns true if unique, false otherwise
        if(!variantList.add(variant.name)) {
            editor.putStringSet(VARIANT_LIST, variantList)
        }

        editor.putString(KEY_PREFIX + variant.name, gson.toJson(variant.configuration))
        editor.apply()
    }

    override fun removeVariant(name: String) {
        val variantList = sharedPreferences.getStringSet(VARIANT_LIST, null)
        variantList.remove(name)
        editor.putStringSet(VARIANT_LIST, variantList)

        editor.remove(KEY_PREFIX + name)
        editor.apply()
    }

    override fun getVariants(): List<Variant> {
        val variantNames = sharedPreferences.getStringSet(VARIANT_LIST, emptySet())
        val variants = ArrayList<Variant>(variantNames.size)

        for(name in variantNames) {
            variants.add(Variant(name, gson.fromJson(sharedPreferences.getString(KEY_PREFIX + name, null), klass)))
        }

        return variants
    }

    override fun getVariant(name: String): Variant? {
        if(sharedPreferences.contains(KEY_PREFIX + name)) {
            return Variant(name, gson.fromJson(sharedPreferences.getString(KEY_PREFIX + name, null), klass))
        } else {
            return null
        }
    }

    override fun setCurrentVariant(name: String) {
        editor.putString(CURRENT_VARIANT, name).apply()
    }

    override fun getCurrentVariant(): Variant? {
        if(!sharedPreferences.contains(CURRENT_VARIANT)) {
            return null
        }

        val name = sharedPreferences.getString(CURRENT_VARIANT, null);

        if(sharedPreferences.contains(KEY_PREFIX + name)) {
            return Variant(name, gson.fromJson(sharedPreferences.getString(KEY_PREFIX + name, null), klass))
        } else {
            return null
        }
    }
}