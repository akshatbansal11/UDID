package com.udid.utilities.interactors

import androidx.databinding.BindingAdapter

open class CustomBindingAdapter {
    companion object {
        /**
         * Shows snack bar
         *
         * @param viewLayout         Layout relative to which snack bar is to be shown
         * @param snackMessageInt    Message resource to be shown in snack bar
         * @param snackMessageString Message to be shown in snack bar
         */
//        @JvmStatic
//        @BindingAdapter(value = ["showSnackBarInt", "showSnackBarString"], requireAll = false)
//        fun showSnackBar(
//            viewLayout: View,
//            snackMessageInt: ObservableInt?,
//            snackMessageString: ObservableString?
//        ) {
//            var message = ""
//            if (snackMessageString != null && !TextUtils.isEmpty(snackMessageString.trimmed)) {
//                message = snackMessageString.trimmed
//                snackMessageString.set("")
//            } else if (snackMessageInt != null && snackMessageInt.get() != 0) {
//                message = viewLayout.resources.getString(snackMessageInt.get())
//                snackMessageInt.set(0)
//            }
//            if (!TextUtils.isEmpty(message)) {
//                Utility.showSnackBar(viewLayout, message)
//            }
//        }


//        @JvmStatic
//        @BindingAdapter("navigationViewHeader", "navigationViewHeaderClick")
//        fun loadNavigationHeader(
//            navigationView: NavigationView?,
//            viewModelMain: ViewModelMain?,
//            onClickNavHeader: GeneralClickListener?
//        ) {
//            if (navigationView != null) {
//                val binding: NavHeaderBinding =
//                    NavHeaderBinding.inflate(LayoutInflater.from(navigationView.context))
//                binding.vmMain = viewModelMain
//                binding.onClickNavHeader = onClickNavHeader
//                navigationView.addHeaderView(binding.root)
//            }
//        }
//
//        @JvmStatic
//        @BindingAdapter("refreshListener")
//        fun refreshListener(
//            swipeRefresh: SwipeRefreshLayout?,
//            refreshListener: OnRefreshListener?
//        ) {
//            swipeRefresh?.setOnRefreshListener(refreshListener)
//        }
//
//        @JvmStatic
//        @BindingAdapter("setEnabledSwipeRefresh")
//        fun setEnabledSwipeRefresh(swipeRefresh: SwipeRefreshLayout?, isEnabled: Boolean) {
//            if (swipeRefresh != null) {
//                swipeRefresh.isEnabled = isEnabled
//            }
//        }
//
//        @JvmStatic
//        @BindingAdapter("setSwipeRefreshing")
//        fun setSwipeRefreshing(swipeRefresh: SwipeRefreshLayout?, toRefresh: Boolean) {
//            if (swipeRefresh != null) {
//                swipeRefresh.isRefreshing = toRefresh
//            }
//        }
//
//        @JvmStatic
//        @BindingAdapter("setSwipeRefreshIndicatorColor")
//        fun setSwipeRefreshIndicatorColor(swipeRefresh: SwipeRefreshLayout?, color: Int) {
//            swipeRefresh?.setColorSchemeResources(color)
//        }
//
//        @JvmStatic
//        @BindingAdapter("setSwipeRefreshBackgroundColor")
//        fun setSwipeRefreshBackgroundColor(swipeRefresh: SwipeRefreshLayout?, color: Int) {
//            swipeRefresh?.setProgressBackgroundColorSchemeResource(color)
//        }
//
//        @JvmStatic
//        @BindingAdapter("addHtmlReqTextHint")
//        fun addHtmlReqTextHint(textView: TextInputLayout?, text: String?) {
//            var text = text
//            if (!TextUtils.isEmpty(text) && textView != null) {
//                text += " " + textView.resources.getString(R.string.text_doc_title)
//                textView.hint = Html.fromHtml(text, Html.FROM_HTML_MODE_COMPACT)
//            }
//        }
//
//        @JvmStatic
//        @BindingAdapter("addHtmlReqText")
//        fun addHtmlReqText(textView: TextView?, text: String?) {
//            var text = text
//            if (!TextUtils.isEmpty(text) && textView != null) {
//                text += " " + textView.resources.getString(R.string.text_doc_title)
//                textView.text = Html.fromHtml(text, Html.FROM_HTML_MODE_COMPACT)
//            }
//        }
//
//        @JvmStatic
//        @BindingAdapter("addDrawableStartCompact")
//        fun addDrawableStartCompact(textView: TextView, drawableResource: Int) {
//            textView.setCompoundDrawablesWithIntrinsicBounds(drawableResource, 0, 0, 0)
//        }
//
//        @JvmStatic
//        @BindingAdapter("addHtmlText")
//        fun addHtmlText(textView: TextView?, text: String?) {
//            if (textView != null) {
//                if (!TextUtils.isEmpty(text)) {
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                        textView.text = Html.fromHtml(text, Html.FROM_HTML_MODE_COMPACT)
//                    } else {
//                        textView.text = Html.fromHtml(text)
//                    }
//                } else {
//                    textView.text = ""
//                }
//            }
//        }
//
//        @JvmStatic
//        @BindingAdapter("addHtmlTextUnderline")
//        fun addHtmlTextUnderline(textView: TextView?, text: String?) {
//            if (textView != null) {
//                if (!TextUtils.isEmpty(text)) {
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                        textView.text = Html.fromHtml(text, Html.FROM_HTML_MODE_COMPACT)
//                    } else {
//                        textView.text = Html.fromHtml(text)
//                    }
//                    if (!TextUtils.equals(text, textView.resources.getString(R.string.text_dash))) {
//                        underLineTextView(textView)
//                    } else {
//                        noUnderLineTextView(textView)
//                    }
//                } else {
//                    textView.text = ""
//                    noUnderLineTextView(textView)
//                }
//            }
//        }
//
//        /**
//         * Sets enable flag for textinput layout
//         *
//         * @param textInputLayout Textinputlayout reference
//         * @param isEnabled       Whether view is enabled or not
//         */
//        @BindingAdapter("setEnabledTIL")
//        fun setTextInputEnabled(textInputLayout: TextInputLayout, isEnabled: Boolean) {
//            textInputLayout.isEnabled = isEnabled
//        }
//
//        /**
//         * Sets error for textinput layout
//         *
//         * @param textInputLayout Textinputlayout reference
//         * @param errorMessage    Error message to be shown
//         */
//        @BindingAdapter("setError")
//        fun setError(textInputLayout: TextInputLayout, errorMessage: Int) {
//            if (errorMessage != 0) {
//                textInputLayout.error = textInputLayout.context.resources.getString(errorMessage)
//            }
//        }
//
//        /**
//         * Validate mobile number with valid code
//         */
//        @JvmStatic
//        @BindingAdapter("setTextChangeMobileListener")
//        fun setTextChangeMobileListener(
//            input: TextInputEditText,
//            message: ObservableString
//        ) {
//            input.addTextChangedListener(object : TextWatcher {
//                override fun beforeTextChanged(
//                    charSequence: CharSequence,
//                    i: Int,
//                    i1: Int,
//                    i2: Int
//                ) {
//                }
//
//                override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
//                    if (charSequence.toString().isNotEmpty()) {
//                        if (charSequence.toString().trim { it <= ' ' }.startsWith("0") ||
//                            charSequence.toString().trim { it <= ' ' }.startsWith("1") ||
//                            charSequence.toString().trim { it <= ' ' }.startsWith("2") ||
//                            charSequence.toString().trim { it <= ' ' }.startsWith("3") ||
//                            charSequence.toString().trim { it <= ' ' }.startsWith("4") ||
//                            charSequence.toString().trim { it <= ' ' }.startsWith("5")
//                        ) {
//                            input.setText("")
//                        } else {
//                            input.error = null
//                            message.set("")
//                        }
//                    }
//                }
//
//                override fun afterTextChanged(editable: Editable) {}
//            })
//        }
//
//        @JvmStatic
//        @BindingAdapter("addSourceCompact")
//        fun addSourceCompact(view: AppCompatImageView, drawableResource: Int) {
//            view.setImageResource(drawableResource)
//        }
//
//        /*@BindingAdapter("customNestedScrollViewOnMeasure")
//    public static void customNestedScrollViewOnMeasure(final CustomNestedScrollView view,
//                                                 final ObservableBoolean observableProgressBar) {
//        CustomNestedScrollView customNestedScrollView = new CustomNestedScrollView(view.getContext());
//        customNestedScrollView.setObservableProgressBar(observableProgressBar);
//    }*/
//        @SuppressLint("CheckResult")
//        @JvmStatic
//        @BindingAdapter(
//            value = ["setImageURL", "isWithoutCache", "imageRes", "imageDrawable", "requestOptions"],
//            requireAll = false
//        )
//        fun bindImageUrl(
//            view: ImageView, imageUrl: String?,
//            isWithoutCache: Boolean, imageRes: Int, imageDrawable: Drawable?,
//            requestOption: RequestOptions?
//        ) {
//            var requestOption = requestOption
//            if (!TextUtils.isEmpty(imageUrl)) {
//                if (requestOption == null) {
//                    requestOption =
//                        (view.context.applicationContext as KnowYourTempleWebApplication)
//                            .glideBaseOptions(R.drawable.ic_image_placeholder)
//                }
//                if (isWithoutCache) {
//                    //TODO: Uncomment code if want to remove cache
//                    /*Glide.with(view.getContext())
//                        .load(imageUrl)
//                        .diskCacheStrategy(DiskCacheStrategy.NONE)
//                        .signature(new ObjectKey(System.currentTimeMillis()))
//                        .skipMemoryCache(true)
//                        .apply(requestOption)
//                        .into(view);*/
//                    Glide.with(view.context)
//                        .load(imageUrl)
//                        .apply(requestOption)
//                        .into(view)
//                } else {
//                    Glide.with(view.context)
//                        .load(imageUrl)
//                        .apply(requestOption)
//                        .into(view)
//                }
//
//                //loadNetworkImage(((MyApplication) view.getContext().getApplicationContext()), view, imageUrl, isWithoutCache, requestOption);
//            } else if (imageRes != 0) {
//                if (requestOption != null) {
//                    Glide.with(view.context)
//                        .load(imageRes)
//                        .apply(requestOption)
//                        .into(view)
//                } else {
//                    view.setImageResource(imageRes)
//                }
//            } else if (imageDrawable != null) {
//                view.setImageDrawable(imageDrawable)
//            } else {
//                val builder = Glide.with(view.context).load("")
//                if (requestOption != null) {
//                    builder.apply(requestOption)
//                } else {
//                    builder.placeholder(R.drawable.ic_image_placeholder).centerCrop()
//                }
//                builder.into(view)
//            }
//        }
//
//        @JvmStatic
//        @BindingAdapter("setRecordFound")
//        fun setRecordFound(
//            view: TextView,
//            itemCount: String?
//        ) {
//            view.text = HtmlCompat.fromHtml(
//                String.format(
//                    view.context.getString(R.string.text_record_found),
//                    itemCount
//                ), HtmlCompat.FROM_HTML_MODE_COMPACT
//            )
//        }
//
//        @JvmStatic
//        @BindingAdapter("setTotalRecords")
//        fun setTotalRecords(
//            view: TextView,
//            itemCount: String?
//        ) {
//            view.text = HtmlCompat.fromHtml(
//                String.format(
//                    view.context.getString(R.string.text_total_records),
//                    itemCount
//                ), HtmlCompat.FROM_HTML_MODE_COMPACT
//            )
//        }
//
//        private fun setCustomLinearAdapter(
//            recyclerView: RecyclerView, adapter: RecyclerView.Adapter<*>,
//            orientation: Int
//        ) {
//            recyclerView.adapter = adapter
//            val customLayoutManager = CustomLayoutManager(recyclerView.context, orientation, false)
//            recyclerView.layoutManager = customLayoutManager
//        }
//
//        private fun setLinearAdapter(
//            recyclerView: RecyclerView, adapter: RecyclerView.Adapter<*>,
//            orientation: Int
//        ) {
//            recyclerView.adapter = adapter
//            val linearLayoutManager = LinearLayoutManager(recyclerView.context, orientation, false)
//            recyclerView.layoutManager = linearLayoutManager
//        }
//
//        private fun setGridAdapter(
//            recyclerView: RecyclerView, adapter: RecyclerView.Adapter<*>,
//            orientation: Int, spanCount: Int
//        ) {
//            recyclerView.adapter = adapter
//            val gridLayoutManager = GridLayoutManager(
//                recyclerView.context,
//                spanCount, orientation, false
//            )
//            recyclerView.layoutManager = gridLayoutManager
//        }
//
//        private fun setAdapter(
//            recyclerView: RecyclerView,
//            adapter: RecyclerView.Adapter<*>,
//            orientation: Int
//        ) {
//            recyclerView.adapter = adapter
//            val linearLayoutManager = LinearLayoutManager(recyclerView.context, orientation, false)
//            recyclerView.layoutManager = linearLayoutManager
//        }
//
//        @JvmStatic
//        @BindingAdapter(
//            "bindAdapterGenreCategories",
//            "bindListenerGenreCategories"
//        )
//        fun bindAdapterGenreCategories(
//            recyclerView: RecyclerView,
//            listData: List<PojoGenreCategoriesData>,
//            itemListener: GeneralItemClickListener
//        ) {
//            if (recyclerView.adapter == null) {
//                val requestOption: RequestOptions =
//                    (recyclerView.context.applicationContext as KnowYourTempleWebApplication)
//                        .glideBaseOptions(R.drawable.ic_image_placeholder_banner)
//                val adapter = AdapterGenreCategories(listData, itemListener, requestOption)
//                setAdapter(recyclerView, adapter, LinearLayoutManager.HORIZONTAL)
//                //recyclerView.scrollToPosition(position);
//            } else {
//                //recyclerView.scrollToPosition(position);
//                recyclerView.adapter!!.notifyDataSetChanged()
//            }
//        }
//
//
//        @JvmStatic
//        @BindingAdapter(
//            "bindAdapterGenreSubCategories",
//            "bindListenerGenreSubCategories",
//        )
//        fun bindAdapterGenreSubCategories(
//            recyclerView: RecyclerView,
//            listData: List<PojoGenreSubCategoriesData>,
//            itemListener: GeneralItemClickListener
//        ) {
//            if (recyclerView.adapter == null) {
//                val adapter = AdapterGenreSubCategories(listData, itemListener)
//                setGridAdapter(recyclerView, adapter, GridLayoutManager.VERTICAL, 3)
////                setAdapter(recyclerView, adapter, LinearLayoutManager.VERTICAL)
//            } else {
//                recyclerView.adapter!!.notifyDataSetChanged()
//            }
//        }
//
//
//        @JvmStatic
//        @BindingAdapter(
//            "bindAdapterViewAllVideos",
//            "bindListenerViewAllVideos",
//        )
//        fun bindAdapterViewAllVideos(
//            recyclerView: RecyclerView,
//            listData: List<PojoGenreVideosData>,
//            itemListener: GeneralItemClickListener
//        ) {
//            if (recyclerView.adapter == null) {
//                val requestOption: RequestOptions =
//                    (recyclerView.context.applicationContext as KnowYourTempleWebApplication)
//                        .glideBaseOptions(R.drawable.ic_image_placeholder_banner)
//                val adapter = AdapterGenreViewAllVideos(listData, itemListener, requestOption)
//                setAdapter(recyclerView, adapter, LinearLayoutManager.VERTICAL)
//            } else {
//                recyclerView.adapter!!.notifyDataSetChanged()
//            }
//        }
//
//
//        @JvmStatic
//        @BindingAdapter(
//            "bindAdapterRelatedVideos",
//            "bindListenerRelatedVideos",
//        )
//        fun bindAdapterRelatedVideos(
//            recyclerView: RecyclerView,
//            listData: List<PojoRelatedVideosData>,
//            itemListener: GeneralItemClickListener
//        ) {
//            if (recyclerView.adapter == null) {
//                val requestOption: RequestOptions =
//                    (recyclerView.context.applicationContext as KnowYourTempleWebApplication)
//                        .glideBaseOptions(R.drawable.ic_image_placeholder_banner)
//                val adapter = AdapterRelatedVideos(listData, itemListener, requestOption)
//                setAdapter(recyclerView, adapter, LinearLayoutManager.VERTICAL)
//            } else {
//                recyclerView.adapter!!.notifyDataSetChanged()
//            }
//        }
//
//
//        @JvmStatic
//        @BindingAdapter(
//            "bindAdapterRashifalList",
//            "bindListenerRashifalList",
//        )
//        fun bindAdapterRashifalList(
//            recyclerView: RecyclerView,
//            listData: List<PojoRashifalListData>,
//            itemListener: GeneralItemClickListener
//        ) {
//            if (recyclerView.adapter == null) {
//                val requestOption: RequestOptions =
//                    (recyclerView.context.applicationContext as KnowYourTempleWebApplication)
//                        .glideBaseOptions(R.drawable.ic_image_placeholder)
//
//                val adapter = AdapterRashifalListData(listData, itemListener, requestOption)
//                setGridAdapter(recyclerView, adapter, GridLayoutManager.VERTICAL, 3)
//            } else {
//                recyclerView.adapter!!.notifyDataSetChanged()
//            }
//        }
//
//        @JvmStatic
//        @BindingAdapter(
//            "setCircleImageView"
//        )
//        fun setCircleImageView(
//            imageView: ImageView,
//            url: String,
//        ) {
//            Glide.with(imageView.context).load(url)
//                .placeholder(R.drawable.ic_image_placeholder_circle)
//                .error(R.drawable.ic_image_placeholder_circle)
//                .diskCacheStrategy(DiskCacheStrategy.ALL)
//                .apply(RequestOptions.circleCropTransform()).into(imageView)
//        }
//
//        @JvmStatic
//        @BindingAdapter(
//            "bindAdapterTempleInfoList",
//            "bindListenerTempleInfoList",
//            "bindScrollListenerTempleInfoList"
//        )
//        fun bindAdapterTempleInfoList(
//            recyclerView: RecyclerView,
//            listData: List<PojoTempleInfoListData>,
//            itemListener: GeneralItemClickListener,
//            onScrollListener: RecyclerView.OnScrollListener
//        ) {
//            if (recyclerView.adapter == null) {
//                val adapter = AdapterTempleInfoList(listData, itemListener)
//                setGridAdapter(recyclerView, adapter, GridLayoutManager.VERTICAL, 2)
//                recyclerView.addOnScrollListener(onScrollListener)
//            } else {
//                recyclerView.adapter!!.notifyDataSetChanged()
//            }
//        }
//
//
//        @JvmStatic
//        @BindingAdapter("bindAdapterWishes", "bindListenerWishes", "bindScrollListenerWishes")
//        fun bindAdapterWishes(
//            recyclerView: RecyclerView,
//            listData: List<PojoWishesData>,
//            itemListener: GeneralItemClickListener,
//            onScrollListener: RecyclerView.OnScrollListener?
//        ) {
//            if (recyclerView.adapter == null) {
//                val requestOption: RequestOptions =
//                    (recyclerView.context.applicationContext as KnowYourTempleWebApplication)
//                        .glideBaseOptions(R.drawable.ic_image_placeholder_banner)
//                val adapter = AdapterWishes(listData, itemListener, requestOption)
//                setAdapter(recyclerView, adapter, LinearLayoutManager.VERTICAL)
//                recyclerView.addOnScrollListener(onScrollListener!!)
//            } else {
//                recyclerView.adapter!!.notifyDataSetChanged()
//            }
//        }
//
//        @JvmStatic
//        @BindingAdapter("bindMusicByCategory", "bindListenerMusic", "bindScrollListener")
//        fun bindMusicByCategory(
//            recyclerView: RecyclerView,
//            listData: List<PojoGetMusicBycategoryData>,
//            itemListener: GeneralItemClickListener,
//            onScrollListener: RecyclerView.OnScrollListener?
//        ) {
//            if (recyclerView.adapter == null) {
//                val requestOption: RequestOptions =
//                    (recyclerView.context.applicationContext as KnowYourTempleWebApplication)
//                        .glideBaseOptions(R.drawable.ic_image_placeholder_banner)
//                val adapter = AdapterMusicByCategoryList(
//                    recyclerView.context,
//                    listData,
//                    itemListener,
//                    requestOption
//                )
//                setAdapter(recyclerView, adapter, LinearLayoutManager.VERTICAL)
//                recyclerView.addOnScrollListener(onScrollListener!!)
//            } else {
//                recyclerView.adapter!!.notifyDataSetChanged()
//            }
//        }
//
//
//        @JvmStatic
//        @BindingAdapter(
//            "bindAdapterMantraCategories",
//            "bindListenerMantraCategories"
//        )
//        fun bindAdapterMantraCategories(
//            recyclerView: RecyclerView,
//            listData: List<PojoMantraCategoriesData>,
//            itemListener: GeneralItemClickListener
//        ) {
//            if (recyclerView.adapter == null) {
//                val requestOption: RequestOptions =
//                    (recyclerView.context.applicationContext as KnowYourTempleWebApplication)
//                        .glideBaseOptions(R.drawable.ic_image_placeholder_banner)
//                val adapter = AdapterMantraCategories(listData, itemListener, requestOption)
//                setAdapter(recyclerView, adapter, LinearLayoutManager.HORIZONTAL)
//                //recyclerView.scrollToPosition(position);
//            } else {
//                //recyclerView.scrollToPosition(position);
//                recyclerView.adapter!!.notifyDataSetChanged()
//            }
//        }
//
//
//        @JvmStatic
//        @BindingAdapter("bindAdapterMantras", "bindListenerMantras", "bindScrollListenerMantras")
//        fun bindAdapterMantras(
//            recyclerView: RecyclerView,
//            listData: List<PojoMantrasByCategoryData>,
//            itemListener: GeneralItemClickListener,
//            onScrollListener: RecyclerView.OnScrollListener?
//        ) {
//            if (recyclerView.adapter == null) {
//                val requestOption: RequestOptions =
//                    (recyclerView.context.applicationContext as KnowYourTempleWebApplication)
//                        .glideBaseOptions(R.drawable.ic_image_placeholder_banner)
//                val adapter = AdapterMantras(listData, itemListener, requestOption)
//                setAdapter(recyclerView, adapter, LinearLayoutManager.VERTICAL)
//                recyclerView.addOnScrollListener(onScrollListener!!)
//            } else {
//                recyclerView.adapter!!.notifyDataSetChanged()
//            }
//        }
//
//
//        @JvmStatic
//        @BindingAdapter("bindAdapterPodcast", "bindListenerPodcast", "bindScrollListenerPodcast")
//        fun bindAdapterPodcast(
//            recyclerView: RecyclerView,
//            listData: List<PojoPodcastData>,
//            itemListener: GeneralItemClickListener,
//            onScrollListener: RecyclerView.OnScrollListener?
//        ) {
//            if (recyclerView.adapter == null) {
//                val requestOption: RequestOptions =
//                    (recyclerView.context.applicationContext as KnowYourTempleWebApplication)
//                        .glideBaseOptions(R.drawable.ic_image_placeholder_banner)
//                val adapter = AdapterPodcasts(listData, itemListener, requestOption)
//                setAdapter(recyclerView, adapter, LinearLayoutManager.VERTICAL)
//                recyclerView.addOnScrollListener(onScrollListener!!)
//            } else {
//                recyclerView.adapter!!.notifyDataSetChanged()
//            }
//        }
//
//        @JvmStatic
//        @BindingAdapter(
//            "bindAdapterHomeMandir",
//            "bindItemListner",
//            "bindlickListner",
//            "bindlCallbackPager",
//        )
//        fun bindAdapterHomeMandir(
//            recyclerView: RecyclerView,
//            listData: List<PojoMandirSublist>,
//            itemListener: GeneralItemClickListener,
//            clickListner: GeneralClickListener,
//            callbackPager: ViewPager2.OnPageChangeCallback,
//        ) {
//            if (recyclerView.adapter == null) {
//                val adapter = AdapterHomeMandir(
//                    recyclerView.context,
//                    listData,
//                    itemListener,
//                    clickListner,
//                    callbackPager
//                )
//                setAdapter(recyclerView, adapter, LinearLayoutManager.VERTICAL)
//            } else {
//                recyclerView.adapter!!.notifyDataSetChanged()
//            }
//        }
//
//        @JvmStatic
//        @BindingAdapter(
//            "bindAdapterMandirBanner",
//            "bindItemListner",
//            "bindlCallbackPager",
//        )
//        fun bindAdapterMandirBanner(
//            viewPager: ViewPager2,
//            listData: List<PojoMandirData>,
//            itemListener: GeneralItemClickListener,
//            callbackPager: ViewPager2.OnPageChangeCallback,
//        ) {
//            if (viewPager.adapter == null) {
//                val requestOption: RequestOptions =
//                    (viewPager.context.applicationContext as KnowYourTempleWebApplication)
//                        .glideBaseOptions(R.drawable.ic_image_placeholder_banner)
//                val adapter = AdapterHomeMandirBanner(
//                    viewPager.context,
//                    listData,
//                    itemListener,
//                    requestOption
//                )
//                viewPager.adapter = adapter
//                val transformer = CompositePageTransformer()
//                transformer.addTransformer(MarginPageTransformer(40))
//                transformer.addTransformer { page, position ->
//                    val r = 1 - abs(position)
//                    page.scaleY = 0.85f + r * 0.14f
//                }
//                viewPager.setPageTransformer(transformer)
//                viewPager.registerOnPageChangeCallback(callbackPager)
//            } else {
//                viewPager.adapter?.notifyDataSetChanged()
//            }
//        }
//
//        @JvmStatic
//        @BindingAdapter(
//            "bindAdapterHomeItem",
//            "bindItemListner",
//        )
//        fun bindAdapterHomeItem(
//            recyclerView: RecyclerView,
//            listData: List<PojoMandirData>,
//            itemListener: GeneralItemClickListener,
//
//            ) {
//            if (recyclerView.adapter == null) {
//                val requestOption: RequestOptions =
//                    (recyclerView.context.applicationContext as KnowYourTempleWebApplication)
//                        .glideBaseOptions(R.drawable.ic_image_placeholder_banner)
//                val adapter = AdapterHomeMandirItem(
//                    recyclerView.context,
//                    listData,
//                    itemListener,
//                    requestOption
//                )
//                setAdapter(recyclerView, adapter, LinearLayoutManager.HORIZONTAL)
//            } else {
//                recyclerView.adapter!!.notifyDataSetChanged()
//            }
//        }
//
//
//        @JvmStatic
//        @BindingAdapter(
//            "bindAdapterHomeShortItem",
//            "bindItemListner",
//        )
//        fun bindAdapterHomeShortItem(
//            recyclerView: RecyclerView,
//            listData: List<PojoMandirData>,
//            itemListener: GeneralItemClickListener,
//
//            ) {
//            if (recyclerView.adapter == null) {
//                val requestOption: RequestOptions =
//                    (recyclerView.context.applicationContext as KnowYourTempleWebApplication)
//                        .glideBaseOptions(R.drawable.ic_image_placeholder_banner)
//                val adapter = AdapterHomeShort(
//                    recyclerView.context,
//                    listData,
//                    itemListener,
//                    requestOption
//                )
//                setAdapter(recyclerView, adapter, LinearLayoutManager.HORIZONTAL)
//            } else {
//                recyclerView.adapter!!.notifyDataSetChanged()
//            }
//        }
//
//        @JvmStatic
//        @BindingAdapter(
//            "bindAdapterHomePhotos",
//            "bindItemListner",
//        )
//        fun bindAdapterHomePhotos(
//            recyclerView: RecyclerView,
//            listData: List<PojoMandirData>,
//            itemListener: GeneralItemClickListener,
//
//            ) {
//            if (recyclerView.adapter == null) {
//                val requestOption: RequestOptions =
//                    (recyclerView.context.applicationContext as KnowYourTempleWebApplication)
//                        .glideBaseOptions(R.drawable.ic_image_placeholder_banner)
//                val adapter = AdapterHomePhoto(
//                    recyclerView.context,
//                    listData,
//                    itemListener,
//                    requestOption
//                )
//                setAdapter(recyclerView, adapter, LinearLayoutManager.HORIZONTAL)
//            } else {
//                recyclerView.adapter!!.notifyDataSetChanged()
//            }
//        }
//
//        @JvmStatic
//        @BindingAdapter(
//            "bindAdapterHomePopularVideos",
//            "bindListenerHomePopularVideos"
//        )
//        fun bindAdapterHomePopularVideos(
//            recyclerView: RecyclerView,
//            listData: List<PojoMandirData>,
//            itemListener: GeneralItemClickListener,
//        ) {
//            if (recyclerView.adapter == null) {
//                val requestOption: RequestOptions =
//                    (recyclerView.context.applicationContext as KnowYourTempleWebApplication)
//                        .glideBaseOptions(R.drawable.ic_image_placeholder_banner)
//                val adapter = AdapterHomeMandirPopularVideos(
//                    recyclerView.context,
//                    listData,
//                    itemListener,
//                    requestOption
//                )
//                setGridAdapter(recyclerView, adapter, GridLayoutManager.VERTICAL, 2)
//            } else {
//                recyclerView.adapter?.notifyDataSetChanged()
//            }
//        }
//
//        @JvmStatic
//        @BindingAdapter(
//            "bindAdapterHomeTrendingTopics",
//            "bindListenerHomeTrendingTopics"
//        )
//        fun bindAdapterHomeTrendingTopics(
//            recyclerView: RecyclerView,
//            listData: List<PojoMandirData>,
//            itemListener: GeneralItemClickListener,
//        ) {
//            if (recyclerView.adapter == null) {
//                val requestOption: RequestOptions =
//                    (recyclerView.context.applicationContext as KnowYourTempleWebApplication)
//                        .glideBaseOptions(R.drawable.ic_image_placeholder_banner)
//                val adapter = AdapterHomeTrending(
//                    recyclerView.context,
//                    listData,
//                    itemListener,
//                    requestOption
//                )
//                setAdapter(recyclerView, adapter, GridLayoutManager.HORIZONTAL)
//            } else {
//                recyclerView.adapter?.notifyDataSetChanged()
//            }
//        }
//
//        @JvmStatic
//        @BindingAdapter(
//            "bindAdapterHomeChantsMantras",
//            "bindListenerHomeChantsMantras"
//        )
//        fun bindAdapterHomeChantsMantras(
//            recyclerView: RecyclerView,
//            listData: List<PojoMandirData>,
//            itemListener: GeneralItemClickListener,
//        ) {
//            if (recyclerView.adapter == null) {
//                val requestOption: RequestOptions =
//                    (recyclerView.context.applicationContext as KnowYourTempleWebApplication)
//                        .glideBaseOptions(R.drawable.ic_image_placeholder_banner)
//                val adapter = AdapterHomeMandirChantsMantras(
//                    recyclerView.context,
//                    listData,
//                    itemListener,
//                    requestOption
//                )
//                recyclerView.adapter = adapter
//                val layoutManager = FlexboxLayoutManager(recyclerView.context)
//                layoutManager.flexDirection = FlexDirection.ROW;
//                layoutManager.flexWrap = FlexWrap.WRAP;
//                layoutManager.justifyContent = JustifyContent.CENTER;
//                recyclerView.layoutManager = layoutManager;
//            } else {
//                recyclerView.adapter?.notifyDataSetChanged()
//            }
//        }
//
//
//        @JvmStatic
//        @BindingAdapter(
//            "bindAdapterMantrasForYou",
//            "bindListenerMantrasForYou"
//        )
//        fun bindAdapterMantrasForYou(
//            recyclerView: RecyclerView,
//            listData: List<PojoMantrasByCategoryData>,
//            itemListener: GeneralItemClickListener
//        ) {
//            if (recyclerView.adapter == null) {
//                val requestOption: RequestOptions =
//                    (recyclerView.context.applicationContext as KnowYourTempleWebApplication)
//                        .glideBaseOptions(R.drawable.ic_image_placeholder)
//                val adapter = AdapterMantrasForYou(listData, itemListener, requestOption)
//                setAdapter(recyclerView, adapter, LinearLayoutManager.HORIZONTAL)
//                //recyclerView.scrollToPosition(position);
//            } else {
//                //recyclerView.scrollToPosition(position);
//                recyclerView.adapter!!.notifyDataSetChanged()
//            }
//        }
//
//
//        @JvmStatic
//        @BindingAdapter("bindAdapterMusicForYou", "bindListenerMusicForYou")
//        fun bindAdapterMusicForYou(
//            recyclerView: RecyclerView,
//            listData: List<PojoGetMusicBycategoryData>,
//            itemListener: GeneralItemClickListener
//        ) {
//            if (recyclerView.adapter == null) {
//                val requestOption: RequestOptions =
//                    (recyclerView.context.applicationContext as KnowYourTempleWebApplication)
//                        .glideBaseOptions(R.drawable.ic_image_placeholder)
//                val adapter =
//                    AdapterMusicForYou(recyclerView.context, listData, itemListener, requestOption)
//                setAdapter(recyclerView, adapter, LinearLayoutManager.VERTICAL)
//            } else {
//                recyclerView.adapter!!.notifyDataSetChanged()
//            }
//        }
//
//        @JvmStatic
//        @BindingAdapter("bindAdapterExplore", "bindListenerExplore")
//        fun bindAdapterExplore(
//            recyclerView: RecyclerView,
//            listData: List<PojoExploreData>,
//            itemListener: GeneralItemClickListener
//        ) {
//            if (recyclerView.adapter == null) {
//                val requestOption: RequestOptions =
//                    (recyclerView.context.applicationContext as KnowYourTempleWebApplication)
//                        .glideBaseOptions(R.drawable.ic_image_placeholder)
//                val adapter =
//                    AdapterExplore(recyclerView.context, listData, itemListener, requestOption)
//                setAdapter(recyclerView, adapter, LinearLayoutManager.VERTICAL)
//            } else {
//                recyclerView.adapter!!.notifyDataSetChanged()
//            }
//        }
//
//        @JvmStatic
//        @BindingAdapter(
//            "bindAdapterTempleInfoDetailsTempleTimings",
//            "bindListenerTempleInfoDetailsTempleTimings"
//        )
//        fun bindAdapterTempleInfoDetailsTempleTimings(
//            recyclerView: RecyclerView,
//            listData: List<PojoTempleTimings>,
//            itemListener: GeneralItemClickListener
//        ) {
//            if (recyclerView.adapter == null) {
//                val adapter =
//                    AdapterTempleInfoDetailsTempleTimingsExplore(
//                        recyclerView.context,
//                        listData,
//                        itemListener
//                    )
//                setAdapter(recyclerView, adapter, LinearLayoutManager.VERTICAL)
//            } else {
//                recyclerView.adapter!!.notifyDataSetChanged()
//            }
//        }
//
//
//        @JvmStatic
//        @BindingAdapter(
//            "bindAdapterFavoriteMantras",
//            "bindListenerFavoriteMantras",
//        )
//        fun bindAdapterFavoriteMantras(
//            recyclerView: RecyclerView,
//            listData: List<PojoFavoriteMantrasData>,
//            itemListener: GeneralItemClickListener
//        ) {
//            if (recyclerView.adapter == null) {
//                val requestOption: RequestOptions =
//                    (recyclerView.context.applicationContext as KnowYourTempleWebApplication)
//                        .glideBaseOptions(R.drawable.ic_image_placeholder)
//                val adapter = AdapterFavoriteMantras(
//                    recyclerView.context,
//                    listData,
//                    itemListener,
//                    requestOption
//                )
//                setAdapter(recyclerView, adapter, LinearLayoutManager.VERTICAL)
//            } else {
//                recyclerView.adapter!!.notifyDataSetChanged()
//            }
//        }
//
//        @JvmStatic
//        @BindingAdapter(
//            "bindAdapterRecentlyWatchedVideos",
//            "bindListenerRecentlyWatchedVideos",
//            "bindScrollListenerRecentlyWatchedVideos"
//        )
//        fun bindAdapterRecentlyWatchedVideos(
//            recyclerView: RecyclerView,
//            listData: List<PojoRecentlyWatchedVideosData>,
//            itemListener: GeneralItemClickListener,
//            scrollListener: RecyclerView.OnScrollListener,
//        ) {
//            if (recyclerView.adapter == null) {
//                val requestOption: RequestOptions =
//                    (recyclerView.context.applicationContext as KnowYourTempleWebApplication)
//                        .glideBaseOptions(R.drawable.ic_image_placeholder)
//                val adapter = AdapterRecentlyWatchedVideos(listData, itemListener, requestOption)
//                setGridAdapter(recyclerView, adapter, GridLayoutManager.VERTICAL, 2)
//                recyclerView.addOnScrollListener(scrollListener)
//            } else {
//                recyclerView.adapter!!.notifyDataSetChanged()
//            }
//        }
//
//
//        @JvmStatic
//        @BindingAdapter(
//            "bindAdapterRegisterForPuja",
//            "bindListenerRegisterForPuja",
//            "bindScrollListenerRegisterForPuja"
//        )
//        fun bindAdapterRegisterForPuja(
//            recyclerView: RecyclerView,
//            listData: List<PojoRegisterForPujaData>,
//            itemListener: GeneralItemClickListener,
//            onScrollListener: RecyclerView.OnScrollListener
//        ) {
//            if (recyclerView.adapter == null) {
//                val requestOption: RequestOptions =
//                    (recyclerView.context.applicationContext as KnowYourTempleWebApplication)
//                        .glideBaseOptions(R.drawable.ic_image_placeholder)
//                val adapter =
//                    AdapterRegisterForPuja(
//                        recyclerView.context,
//                        listData,
//                        itemListener,
//                        requestOption
//                    )
//                setAdapter(recyclerView, adapter, LinearLayoutManager.VERTICAL)
//                recyclerView.addOnScrollListener(onScrollListener)
//            } else {
//                recyclerView.adapter!!.notifyDataSetChanged()
//            }
//        }
//
//        @JvmStatic
//        @BindingAdapter("bindAdapterTemplePujaTimings", "bindListenerTemplePujaTimings")
//        fun bindAdapterTemplePujaTimings(
//            recyclerView: RecyclerView,
//            listData: List<String>,
//            itemListener: GeneralItemClickListener
//        ) {
//            if (recyclerView.adapter == null) {
//                val adapter =
//                    AdapterTemplePujaTimings(recyclerView.context, listData, itemListener)
//                setAdapter(recyclerView, adapter, LinearLayoutManager.HORIZONTAL)
//            } else {
//                recyclerView.adapter!!.notifyDataSetChanged()
//            }
//        }
//
//        @JvmStatic
//        @BindingAdapter("bindAdapterPoojaPlans", "bindListener")
//        fun bindAdapterPoojaPlans(
//            recyclerView: RecyclerView,
//            listData: List<PojoPoojaPlanForRegister>,
//            itemListener: GeneralItemClickListener
//        ) {
//            if (recyclerView.adapter == null) {
//                val adapter =
//                    AdapterPoojaplanForRegsitration(recyclerView.context, listData, itemListener)
//                setAdapter(recyclerView, adapter, LinearLayoutManager.VERTICAL)
//            } else {
//                recyclerView.adapter!!.notifyDataSetChanged()
//            }
//        }
//
//        @JvmStatic
//        @BindingAdapter("bindAdapterPoojaPlansInfo", "bindListener")
//        fun bindAdapterPoojaPlansInfo(
//            recyclerView: RecyclerView,
//            listData: List<String>,
//            itemListener: GeneralItemClickListener
//        ) {
//            if (recyclerView.adapter == null) {
//                val adapter =
//                    AdapterPoojaplanInfoForRegsitration(
//                        recyclerView.context,
//                        listData,
//                        itemListener
//                    )
//                setAdapter(recyclerView, adapter, LinearLayoutManager.VERTICAL)
//            } else {
//                recyclerView.adapter!!.notifyDataSetChanged()
//            }
//        }
//
//        @JvmStatic
//        @BindingAdapter("bindAdapterPoojaPlansSummary", "bindListener")
//        fun bindAdapterPoojaPlansSummary(
//            recyclerView: RecyclerView,
//            listData: List<PojoPoojaPlanForRegister>,
//            itemListener: GeneralItemClickListener
//        ) {
//            if (recyclerView.adapter == null) {
//                val adapter =
//                    AdapterPoojaplanSummary(
//                        recyclerView.context,
//                        listData,
//                        itemListener
//                    )
//                setAdapter(recyclerView, adapter, LinearLayoutManager.VERTICAL)
//            } else {
//                recyclerView.adapter!!.notifyDataSetChanged()
//            }
//        }
//
//
//        @JvmStatic
//        @BindingAdapter("bindAdapterPackageExtraFees", "bindListener")
//        fun bindAdapterPackageExtraFees(
//            recyclerView: RecyclerView,
//            listData: List<PojoPackageExtraFee>,
//            itemListener: GeneralItemClickListener
//        ) {
//            if (recyclerView.adapter == null) {
//                val adapter =
//                    AdapterPackageExtraFees(
//                        recyclerView.context,
//                        listData,
//                        itemListener
//                    )
//                setAdapter(recyclerView, adapter, LinearLayoutManager.VERTICAL)
//            } else {
//                recyclerView.adapter!!.notifyDataSetChanged()
//            }
//        }
//
//        @JvmStatic
//        @BindingAdapter("bindAdapterGenderType", "bindListener")
//        fun bindAdapterGenderType(
//            recyclerView: RecyclerView,
//            listData: List<PojoGender>,
//            itemListener: GeneralItemClickListener
//        ) {
//            if (recyclerView.adapter == null) {
//                val adapter =
//                    AdapterGenderCategory(
//                        recyclerView.context,
//                        listData,
//                        itemListener
//                    )
//                setAdapter(recyclerView, adapter, LinearLayoutManager.HORIZONTAL)
//            } else {
//                recyclerView.adapter!!.notifyDataSetChanged()
//            }
//        }
//
//        @JvmStatic
//        @BindingAdapter("setStrikeText")
//        fun setStrikeText(textView: TextView, value: String) {
//            if (!TextUtils.isEmpty(value)) {
//                textView.text = Html.fromHtml(value, Html.FROM_HTML_MODE_COMPACT)
//                textView.text = value
//                textView.paintFlags = textView.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
//            } else {
//                textView.text = ""
//                textView.paintFlags = 0
//            }
//        }
//
//        @JvmStatic
//        @BindingAdapter("adapterSpinnerRashi")
//        open fun adapterSpinnerRashi(
//            view: Spinner,
//            arrayList: ObservableList<PojoRashifalListData?>
//        ) {
//            val adapter: ArrayAdapter<PojoRashifalListData> = ArrayAdapter(
//                view.context,
//                R.layout.spinner_select_item, R.id.txtSpinnerText, arrayList
//            )
//            adapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
//            view.adapter = adapter
//        }

//        @JvmStatic
//        @BindingAdapter("bindOnItemSelectListener")
//        open fun bindOnItemSelectListener(
//            view: Spinner,
//            spinnerSelectedListener: AdapterView.OnItemSelectedListener?
//        ) {
//            view.onItemSelectedListener = spinnerSelectedListener
//        }
    }
}