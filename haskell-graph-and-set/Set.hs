module Set
  ( Set (..),
    empty,
    null,
    singleton,
    union,
    fromList,
    member,
    toList,
    toAscList,
    elems,
    deduplicate,
  )
where

import Data.List (sort)
import Prelude hiding (null)

data Set a
  = Empty
  | Singleton a
  | Union (Set a) (Set a)

empty :: Set a
empty = Empty

null :: Set a -> Bool
null Empty = True
null _ = False

member :: (Eq a) => a -> Set a -> Bool
member _ Empty = False
member a (Singleton b) = a == b
member a (Union l r) = member a l || member a r

singleton :: a -> Set a
singleton = Singleton

fromList :: [a] -> Set a
fromList = foldr insert Empty

toList :: Set a -> [a]
toList Empty = []
toList (Singleton a) = [a]
toList (Union a b) = toList a ++ toList b

toAscList :: (Ord a) => Set a -> [a]
toAscList = removeDuplicates . sort . toList
  where
    removeDuplicates :: (Eq a) => [a] -> [a]
    removeDuplicates [] = []
    removeDuplicates [x] = [x]
    removeDuplicates (x : y : xs)
      | x == y = removeDuplicates (y : xs)
      | otherwise = x : removeDuplicates (y : xs)

deduplicate :: (Ord a) => Set a -> Set a
deduplicate = fromList . toAscList

elems :: Set a -> [a]
elems = toList

union :: Set a -> Set a -> Set a
union Empty s = s
union s Empty = s
union s1 s2 = fromList (toList s1 ++ toList s2)

insert :: a -> Set a -> Set a
insert a = Union (Singleton a)

instance (Ord a) => Eq (Set a) where
  s1 == s2 = toAscList s1 == toAscList s2

instance Semigroup (Set a) where
  s1 <> s2 = s1 `union` s2

instance Monoid (Set a) where
  mempty = empty

instance (Show a) => Show (Set a) where
  show s = show (toList s)

instance Functor Set where
  fmap _ Empty = Empty
  fmap f (Singleton a) = Singleton (f a)
  fmap f (Union l r) = Union (fmap f l) (fmap f r)
